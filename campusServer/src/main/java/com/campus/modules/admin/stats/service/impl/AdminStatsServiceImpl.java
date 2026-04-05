package com.campus.modules.admin.stats.service.impl;

import com.campus.modules.admin.stats.dto.AdminChartPointDTO;
import com.campus.modules.admin.stats.dto.AdminStatsOverviewDTO;
import com.campus.modules.admin.stats.service.AdminStatsService;
import com.campus.common.utils.UploadPaths;
import com.campus.modules.order.entity.OrderMain;
import com.campus.modules.order.mapper.OrderRepository;
import com.campus.modules.product.entity.Product;
import com.campus.modules.product.mapper.ProductRepository;
import com.campus.modules.user.mapper.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserAccountRepository userAccountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminStatsServiceImpl(UserAccountRepository userAccountRepository,
                                 ProductRepository productRepository,
                                 OrderRepository orderRepository) {
        this.userAccountRepository = userAccountRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public AdminStatsOverviewDTO getOverview(String startTime, String endTime) {
        AdminStatsOverviewDTO dto = new AdminStatsOverviewDTO();
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);

        List<OrderMain> orders = orderRepository.findAll();
        long todayCount = orders.stream()
                .map(OrderMain::getCreatedAt)
                .filter(t -> t != null && t.toLocalDate().isEqual(today))
                .count();
        long sevenDayCount = orders.stream()
                .map(OrderMain::getCreatedAt)
                .filter(t -> t != null
                        && !t.toLocalDate().isBefore(sevenDaysAgo)
                        && !t.toLocalDate().isAfter(today))
                .count();

        dto.setTodayOrderCount(todayCount);
        dto.setSevenDayOrderCount(sevenDayCount);

        dto.setTotalUserCount(userAccountRepository.count());
        // 暂时将“卖家总数”按用户总数统计（后续接入卖家认证/角色字段后再区分）
        dto.setTotalSellerCount(userAccountRepository.count());
        dto.setTotalProductCount(productRepository.count());
        return dto;
    }

    @Override
    public List<AdminChartPointDTO> listOrderTrend(String startTime, String endTime) {
        LocalDate[] range = resolveRange(startTime, endTime);
        LocalDate start = range[0];
        LocalDate end = range[1];

        List<LocalDateTime> times = orderRepository.findAll().stream()
                .map(OrderMain::getCreatedAt)
                .filter(t -> t != null
                        && !t.toLocalDate().isBefore(start)
                        && !t.toLocalDate().isAfter(end))
                .collect(Collectors.toList());
        return lastDaysSeries(times, start, end);
    }

    @Override
    public List<AdminChartPointDTO> listUserGrowth(String startTime, String endTime) {
        LocalDate[] range = resolveRange(startTime, endTime);
        LocalDate start = range[0];
        LocalDate end = range[1];

        List<LocalDateTime> times = userAccountRepository.findAll().stream()
                .map(u -> u.getRegisterTime())
                .filter(t -> t != null
                        && !t.toLocalDate().isBefore(start)
                        && !t.toLocalDate().isAfter(end))
                .collect(Collectors.toList());
        return lastDaysSeries(times, start, end);
    }

    @Override
    public List<AdminChartPointDTO> listCategoryDistribution(String startTime, String endTime) {
        // 分类占比：按 product.category_id 粗略分布（来自数据库），暂不按时间过滤
        Map<String, Long> grouped = productRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getCategoryId() == null ? "未分类" : String.valueOf(p.getCategoryId()),
                        Collectors.counting()));

        List<AdminChartPointDTO> list = new ArrayList<>();
        grouped.forEach((k, v) -> {
            AdminChartPointDTO p = new AdminChartPointDTO();
            p.setLabel(k);
            p.setValue(v);
            list.add(p);
        });
        return list;
    }

    @Override
    public String export(String startTime, String endTime) {
        AdminStatsOverviewDTO overview = getOverview(startTime, endTime);
        List<AdminChartPointDTO> orders = listOrderTrend(startTime, endTime);
        List<AdminChartPointDTO> users = listUserGrowth(startTime, endTime);
        List<AdminChartPointDTO> cats = listCategoryDistribution(startTime, endTime);

        String filename = "admin-stats-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + ".csv";
        Path uploadDir = UploadPaths.getUploadDir();
        try {
            Files.createDirectories(uploadDir);
            Path file = uploadDir.resolve(filename);
            StringBuilder sb = new StringBuilder();
            sb.append("统计项,值").append("\n");
            sb.append("今日订单数,").append(overview.getTodayOrderCount()).append("\n");
            sb.append("近7日订单数,").append(overview.getSevenDayOrderCount()).append("\n");
            sb.append("用户总数,").append(overview.getTotalUserCount()).append("\n");
            sb.append("卖家总数,").append(overview.getTotalSellerCount()).append("\n");
            sb.append("商品总数,").append(overview.getTotalProductCount()).append("\n");

            sb.append("\n订单趋势,").append("\n");
            sb.append("日期,订单数").append("\n");
            for (AdminChartPointDTO p : orders) {
                sb.append(escape(p.getLabel())).append(',').append(p.getValue()).append("\n");
            }

            sb.append("\n用户增长,").append("\n");
            sb.append("日期,新增用户").append("\n");
            for (AdminChartPointDTO p : users) {
                sb.append(escape(p.getLabel())).append(',').append(p.getValue()).append("\n");
            }

            sb.append("\n分类分布,").append("\n");
            sb.append("分类,数量").append("\n");
            for (AdminChartPointDTO p : cats) {
                sb.append(escape(p.getLabel())).append(',').append(p.getValue()).append("\n");
            }

            byte[] bom = new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
            byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
            byte[] out = new byte[bom.length + body.length];
            System.arraycopy(bom, 0, out, 0, bom.length);
            System.arraycopy(body, 0, out, bom.length, body.length);
            Files.write(file, out);
            return "/upload/" + filename;
        } catch (Exception e) {
            return null;
        }
    }

    private String escape(String v) {
        if (v == null) return "";
        String s = v.replace("\r", " ").replace("\n", " ");
        if (s.contains(",") || s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    private List<AdminChartPointDTO> lastDaysSeries(List<LocalDateTime> times, LocalDate start, LocalDate end) {
        Map<LocalDate, Long> countMap = times.stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate, Collectors.counting()));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        List<AdminChartPointDTO> list = new ArrayList<>();
        long days = ChronoUnit.DAYS.between(start, end);
        if (days < 0) {
            return list;
        }
        for (int i = 0; i <= days; i++) {
            LocalDate d = start.plusDays(i);
            AdminChartPointDTO p = new AdminChartPointDTO();
            p.setLabel(fmt.format(d));
            p.setValue(countMap.getOrDefault(d, 0L));
            list.add(p);
        }
        return list;
    }

    private LocalDate[] resolveRange(String startTime, String endTime) {
        LocalDate today = LocalDate.now();
        LocalDate defaultStart = today.minusDays(6);
        LocalDate defaultEnd = today;

        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate start = defaultStart;
        LocalDate end = defaultEnd;

        try {
            if (startTime != null && !startTime.isEmpty()) {
                start = LocalDate.parse(startTime, fmt);
            }
        } catch (DateTimeParseException ignored) {
        }
        try {
            if (endTime != null && !endTime.isEmpty()) {
                end = LocalDate.parse(endTime, fmt);
            }
        } catch (DateTimeParseException ignored) {
        }

        if (end.isBefore(start)) {
            end = start;
        }
        return new LocalDate[]{start, end};
    }
}

