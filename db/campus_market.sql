/*
 Navicat Premium Dump SQL

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80026 (8.0.26)
 Source Host           : localhost:3306
 Source Schema         : campus_market

 Target Server Type    : MySQL
 Target Server Version : 80026 (8.0.26)
 File Encoding         : 65001

 Date: 04/03/2026 19:33:04
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `subtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort_order` int NULL DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '开学季数码专场', '二手数码好物精选', '/upload/banner_01.jpg', 1, 1);
INSERT INTO `banner` VALUES (2, '考研资料专场', '高数英政真题低价转让', '/upload/banner_02.jpg', 2, 1);
INSERT INTO `banner` VALUES (3, '毕业季清仓', '毕业生一口价甩卖', '/upload/banner_03.jpg', 3, 1);
INSERT INTO `banner` VALUES (4, '宿舍神器集合', '台灯/收纳/插排', '/upload/banner_04.jpg', 4, 1);
INSERT INTO `banner` VALUES (5, '精品电脑专区', '轻薄本/游戏本任选', '/upload/banner_05.jpg', 5, 1);
INSERT INTO `banner` VALUES (6, '手机专场', '二手旗舰机低价换机', '/upload/banner_06.jpg', 6, 1);
INSERT INTO `banner` VALUES (7, '运动装备专场', '球鞋球拍/护具', '/upload/banner_07.jpg', 7, 1);
INSERT INTO `banner` VALUES (8, '乐器角', '吉他/尤克里里/键盘', '/upload/banner_08.jpg', 8, 1);
INSERT INTO `banner` VALUES (9, '图书角', '专业教材与课外读物', '/upload/banner_09.jpg', 9, 1);
INSERT INTO `banner` VALUES (10, '租赁专区', '短租电器/乐器/行李箱', '/upload/banner_10.jpg', 10, 1);
INSERT INTO `banner` VALUES (11, '拼车信息', '周末放假拼车回家', '/upload/banner_11.jpg', 11, 1);
INSERT INTO `banner` VALUES (12, '兼职信息', '靠谱校内兼职', '/upload/banner_12.jpg', 12, 1);
INSERT INTO `banner` VALUES (13, '实验器材', '实验服/护目镜/工具', '/upload/banner_13.jpg', 13, 1);
INSERT INTO `banner` VALUES (14, '摄影专区', '相机/镜头/三脚架', '/upload/banner_14.jpg', 14, 1);
INSERT INTO `banner` VALUES (15, '其它闲置', '更多好物等你发现', '/upload/banner_15.jpg', 15, 1);

-- ----------------------------
-- Table structure for browse_history
-- ----------------------------
DROP TABLE IF EXISTS `browse_history`;
CREATE TABLE `browse_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `viewed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of browse_history
-- ----------------------------
INSERT INTO `browse_history` VALUES (1, 1, 3001, '2025-09-01 20:30:00');
INSERT INTO `browse_history` VALUES (2, 2, 3002, '2025-09-01 20:31:00');
INSERT INTO `browse_history` VALUES (3, 3, 3003, '2025-09-01 20:32:00');
INSERT INTO `browse_history` VALUES (4, 4, 3004, '2025-09-01 20:33:00');
INSERT INTO `browse_history` VALUES (5, 5, 3005, '2025-09-01 20:34:00');
INSERT INTO `browse_history` VALUES (6, 6, 3006, '2025-09-01 20:35:00');
INSERT INTO `browse_history` VALUES (7, 7, 3007, '2025-09-01 20:36:00');
INSERT INTO `browse_history` VALUES (8, 8, 3008, '2025-09-01 20:37:00');
INSERT INTO `browse_history` VALUES (9, 9, 3009, '2025-09-01 20:38:00');
INSERT INTO `browse_history` VALUES (10, 10, 3010, '2025-09-01 20:39:00');
INSERT INTO `browse_history` VALUES (11, 11, 3011, '2025-09-01 20:40:00');
INSERT INTO `browse_history` VALUES (12, 12, 3012, '2025-09-01 20:41:00');
INSERT INTO `browse_history` VALUES (13, 13, 3013, '2025-09-01 20:42:00');
INSERT INTO `browse_history` VALUES (14, 14, 3014, '2025-09-01 20:43:00');
INSERT INTO `browse_history` VALUES (15, 15, 3015, '2025-09-01 20:44:00');

-- ----------------------------
-- Table structure for campus
-- ----------------------------
DROP TABLE IF EXISTS `campus`;
CREATE TABLE `campus`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `city` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus
-- ----------------------------
INSERT INTO `campus` VALUES (1, '东校区', '南京', 1);
INSERT INTO `campus` VALUES (2, '西校区', '南京', 1);
INSERT INTO `campus` VALUES (3, '南校区', '南京', 1);
INSERT INTO `campus` VALUES (4, '北校区', '南京', 1);
INSERT INTO `campus` VALUES (5, '本部校区', '南京', 1);
INSERT INTO `campus` VALUES (6, '城南校区', '南京', 1);
INSERT INTO `campus` VALUES (7, '城北校区', '南京', 1);
INSERT INTO `campus` VALUES (8, '新城校区', '苏州', 1);
INSERT INTO `campus` VALUES (9, '科技园校区', '苏州', 1);
INSERT INTO `campus` VALUES (10, '湖畔校区', '杭州', 1);
INSERT INTO `campus` VALUES (11, '滨江校区', '杭州', 1);
INSERT INTO `campus` VALUES (12, '大学城一期', '广州', 1);
INSERT INTO `campus` VALUES (13, '大学城二期', '广州', 1);
INSERT INTO `campus` VALUES (14, '闵行校区', '上海', 1);
INSERT INTO `campus` VALUES (15, '徐汇校区', '上海', 1);

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (2, 2, 3002, 2, '2025-09-01 18:01:00');
INSERT INTO `cart_item` VALUES (3, 3, 3003, 1, '2025-09-01 18:02:00');
INSERT INTO `cart_item` VALUES (4, 4, 3004, 1, '2025-09-01 18:03:00');
INSERT INTO `cart_item` VALUES (5, 5, 3005, 1, '2025-09-01 18:04:00');
INSERT INTO `cart_item` VALUES (6, 6, 3006, 3, '2025-09-01 18:05:00');
INSERT INTO `cart_item` VALUES (7, 7, 3007, 1, '2025-09-01 18:06:00');
INSERT INTO `cart_item` VALUES (8, 8, 3008, 2, '2025-09-01 18:07:00');
INSERT INTO `cart_item` VALUES (9, 9, 3009, 1, '2025-09-01 18:08:00');
INSERT INTO `cart_item` VALUES (10, 10, 3010, 1, '2025-09-01 18:09:00');
INSERT INTO `cart_item` VALUES (11, 11, 3011, 1, '2025-09-01 18:10:00');
INSERT INTO `cart_item` VALUES (12, 12, 3012, 1, '2025-09-01 18:11:00');
INSERT INTO `cart_item` VALUES (13, 13, 3013, 1, '2025-09-01 18:12:00');
INSERT INTO `cart_item` VALUES (14, 14, 3014, 1, '2025-09-01 18:13:00');
INSERT INTO `cart_item` VALUES (15, 15, 3015, 1, '2025-09-01 18:14:00');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sort_order` int NULL DEFAULT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, NULL, '数码', 1, 1);
INSERT INTO `category` VALUES (2, NULL, '图书教材', 2, 1);
INSERT INTO `category` VALUES (3, NULL, '生活用品', 3, 1);
INSERT INTO `category` VALUES (4, NULL, '运动装备', 4, 1);
INSERT INTO `category` VALUES (5, NULL, '服饰鞋帽', 5, 1);
INSERT INTO `category` VALUES (6, NULL, '乐器', 6, 1);
INSERT INTO `category` VALUES (7, NULL, '其它', 7, 1);
INSERT INTO `category` VALUES (8, 1, '手机', 10, 1);
INSERT INTO `category` VALUES (9, 1, '电脑/平板', 11, 1);
INSERT INTO `category` VALUES (10, 1, '摄影/配件', 12, 1);
INSERT INTO `category` VALUES (11, 2, '专业教材', 20, 1);
INSERT INTO `category` VALUES (12, 2, '考研资料', 21, 1);
INSERT INTO `category` VALUES (13, 3, '宿舍电器', 30, 1);
INSERT INTO `category` VALUES (14, 3, '收纳整理', 31, 1);
INSERT INTO `category` VALUES (15, 4, '球类运动', 40, 1);

-- ----------------------------
-- Table structure for chat_message
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `from_user_id` bigint NOT NULL,
  `to_user_id` bigint NOT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_chat_message_session_time`(`session_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_message
-- ----------------------------
INSERT INTO `chat_message` VALUES (1, 1, 1, 2, '这本高数教材还有吗？', '2025-09-15 09:55:00');
INSERT INTO `chat_message` VALUES (2, 1, 2, 1, '有的，东区图书馆门口面交。', '2025-09-15 10:00:00');
INSERT INTO `chat_message` VALUES (3, 2, 1, 3, 'iPad 可以小刀吗？', '2025-09-15 10:03:00');
INSERT INTO `chat_message` VALUES (4, 2, 3, 1, '已经是最低价了哦，含原装笔。', '2025-09-15 10:05:00');
INSERT INTO `chat_message` VALUES (5, 3, 7, 2, '习题册单卖吗？', '2025-09-15 10:10:00');
INSERT INTO `chat_message` VALUES (6, 4, 8, 3, '手机几成新？', '2025-09-15 10:15:00');
INSERT INTO `chat_message` VALUES (7, 5, 9, 4, '电脑续航怎么样？', '2025-09-15 10:20:00');

-- ----------------------------
-- Table structure for chat_session
-- ----------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `buyer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  `last_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `last_time` datetime NULL DEFAULT NULL,
  `unread_buyer` int NOT NULL DEFAULT 0,
  `unread_seller` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_chat_session`(`buyer_id` ASC, `seller_id` ASC, `product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_session
-- ----------------------------
INSERT INTO `chat_session` VALUES (1, 1, 2, 3001, '这本高数教材还有吗？', '2025-09-15 10:00:00', 0, 1);
INSERT INTO `chat_session` VALUES (2, 1, 3, 3003, 'iPad 可以小刀吗？', '2025-09-15 10:05:00', 0, 0);
INSERT INTO `chat_session` VALUES (3, 7, 2, 3002, '习题册单卖吗？', '2025-09-15 10:10:00', 0, 0);
INSERT INTO `chat_session` VALUES (4, 8, 3, 3004, '手机几成新？', '2025-09-15 10:15:00', 0, 0);
INSERT INTO `chat_session` VALUES (5, 9, 4, 3005, '电脑续航怎么样？', '2025-09-15 10:20:00', 0, 0);

-- ----------------------------
-- Table structure for favorite_category
-- ----------------------------
DROP TABLE IF EXISTS `favorite_category`;
CREATE TABLE `favorite_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8016 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite_category
-- ----------------------------
INSERT INTO `favorite_category` VALUES (8001, 1, '教材资料', '2025-09-01 19:00:00');
INSERT INTO `favorite_category` VALUES (8002, 1, '数码', '2025-09-01 19:10:00');
INSERT INTO `favorite_category` VALUES (8003, 2, '我的上架', '2025-09-01 19:20:00');
INSERT INTO `favorite_category` VALUES (8004, 3, '想买清单', '2025-09-01 19:30:00');
INSERT INTO `favorite_category` VALUES (8005, 4, '收藏夹A', '2025-09-01 19:40:00');
INSERT INTO `favorite_category` VALUES (8006, 5, '收藏夹B', '2025-09-01 19:50:00');
INSERT INTO `favorite_category` VALUES (8007, 6, '运动装备', '2025-09-01 20:00:00');
INSERT INTO `favorite_category` VALUES (8008, 7, '宿舍用品', '2025-09-01 20:10:00');
INSERT INTO `favorite_category` VALUES (8009, 8, '电子产品', '2025-09-01 20:20:00');
INSERT INTO `favorite_category` VALUES (8010, 9, '考研资料', '2025-09-01 20:30:00');
INSERT INTO `favorite_category` VALUES (8011, 10, '相机配件', '2025-09-01 20:40:00');
INSERT INTO `favorite_category` VALUES (8012, 11, '乐器', '2025-09-01 20:50:00');
INSERT INTO `favorite_category` VALUES (8013, 12, '鞋服', '2025-09-01 21:00:00');
INSERT INTO `favorite_category` VALUES (8014, 13, '杂物', '2025-09-01 21:10:00');
INSERT INTO `favorite_category` VALUES (8015, 14, '待比较', '2025-09-01 21:20:00');

-- ----------------------------
-- Table structure for favorite_item
-- ----------------------------
DROP TABLE IF EXISTS `favorite_item`;
CREATE TABLE `favorite_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite_item
-- ----------------------------
INSERT INTO `favorite_item` VALUES (1, 1, 8001, 3001, '2025-09-01 20:10:00');
INSERT INTO `favorite_item` VALUES (2, 1, 8002, 3003, '2025-09-01 20:11:00');
INSERT INTO `favorite_item` VALUES (3, 2, 8003, 3002, '2025-09-01 20:12:00');
INSERT INTO `favorite_item` VALUES (4, 3, 8004, 3004, '2025-09-01 20:13:00');
INSERT INTO `favorite_item` VALUES (5, 4, 8005, 3005, '2025-09-01 20:14:00');
INSERT INTO `favorite_item` VALUES (6, 5, 8006, 3006, '2025-09-01 20:15:00');
INSERT INTO `favorite_item` VALUES (7, 6, 8007, 3009, '2025-09-01 20:16:00');
INSERT INTO `favorite_item` VALUES (8, 7, 8008, 3007, '2025-09-01 20:17:00');
INSERT INTO `favorite_item` VALUES (9, 8, 8009, 3003, '2025-09-01 20:18:00');
INSERT INTO `favorite_item` VALUES (10, 9, 8010, 3013, '2025-09-01 20:19:00');
INSERT INTO `favorite_item` VALUES (11, 10, 8011, 3012, '2025-09-01 20:20:00');
INSERT INTO `favorite_item` VALUES (12, 11, 8012, 3011, '2025-09-01 20:21:00');
INSERT INTO `favorite_item` VALUES (13, 12, 8013, 3009, '2025-09-01 20:22:00');
INSERT INTO `favorite_item` VALUES (14, 13, 8014, 3014, '2025-09-01 20:23:00');
INSERT INTO `favorite_item` VALUES (15, 14, 8015, 3015, '2025-09-01 20:24:00');
INSERT INTO `favorite_item` VALUES (16, 1, 0, 3006, '2026-03-04 08:55:04');

-- ----------------------------
-- Table structure for forum_comment
-- ----------------------------
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE `forum_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `like_count` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_comment
-- ----------------------------
INSERT INTO `forum_comment` VALUES (1, '支持，开学季确实收二手更划算。', '2025-09-16 10:20:00.000000', 1, 7, 2);
INSERT INTO `forum_comment` VALUES (2, '插排一定注意功率和质量。', '2025-09-16 11:30:00.000000', 0, 7, 3);
INSERT INTO `forum_comment` VALUES (3, '我这有一部分线代笔记，需要的话私聊。', '2025-09-16 11:10:00.000000', 0, 8, 1);
INSERT INTO `forum_comment` VALUES (4, '概率论真题我也在找，蹲。', '2025-09-16 12:20:00.000000', 0, 8, 4);
INSERT INTO `forum_comment` VALUES (5, '补充：查 iCloud/账户锁也很重要。', '2025-09-16 15:05:00.000000', 2, 9, 6);
INSERT INTO `forum_comment` VALUES (6, '面交最好带充电线和电脑，现场验更全。', '2025-09-16 16:30:00.000000', 1, 9, 8);
INSERT INTO `forum_comment` VALUES (7, '收藏了，后续换机用得上。', '2025-09-16 18:10:00.000000', 0, 9, 10);
INSERT INTO `forum_comment` VALUES (8, '同感，别冲动消费，刚需再买。', '2025-09-17 10:10:00.000000', 1, 10, 12);
INSERT INTO `forum_comment` VALUES (9, 'iPad 配合 Goodnotes/Notability 很香。', '2025-09-17 10:45:00.000000', 0, 10, 2);
INSERT INTO `forum_comment` VALUES (10, '冰箱噪音真的看运气，二手要试机。', '2025-09-17 12:05:00.000000', 1, 11, 5);
INSERT INTO `forum_comment` VALUES (11, '功率超了会跳闸，宿舍先问清楚。', '2025-09-17 12:55:00.000000', 0, 11, 7);
INSERT INTO `forum_comment` VALUES (12, '拍子二手建议看框漆是否开裂。', '2025-09-17 15:10:00.000000', 1, 12, 6);
INSERT INTO `forum_comment` VALUES (13, '新手先买耐打线，别追高端。', '2025-09-17 16:10:00.000000', 0, 12, 9);
INSERT INTO `forum_comment` VALUES (14, '公共场所交易 + 当面转账确认，很关键。', '2025-09-18 10:20:00.000000', 2, 13, 1);
INSERT INTO `forum_comment` VALUES (15, '建议平台内聊天留痕，有纠纷好处理。', '2025-09-18 12:05:00.000000', 1, 13, 3);
INSERT INTO `forum_comment` VALUES (16, '两周租赁可以，押金记得写清楚。', '2025-09-18 11:00:00.000000', 0, 14, 2);
INSERT INTO `forum_comment` VALUES (17, '有异味的收纳箱不建议要，影响宿舍环境。', '2025-09-18 16:10:00.000000', 0, 15, 4);
INSERT INTO `forum_comment` VALUES (18, '透明的找东西更方便，赞同。', '2025-09-18 18:00:00.000000', 0, 15, 5);
INSERT INTO `forum_comment` VALUES (19, '标题写清楚成色和交易方式，确实更快。', '2025-09-19 10:30:00.000000', 1, 16, 7);
INSERT INTO `forum_comment` VALUES (20, '最好再补一句“可小刀/不刀”。', '2025-09-19 13:10:00.000000', 0, 16, 8);
INSERT INTO `forum_comment` VALUES (21, '打包视频太重要了，少很多扯皮。', '2025-09-19 11:20:00.000000', 1, 17, 9);
INSERT INTO `forum_comment` VALUES (22, '贵重物品建议走顺丰保价。', '2025-09-19 12:40:00.000000', 0, 17, 10);
INSERT INTO `forum_comment` VALUES (23, '我有操作系统教材，笔记不多。', '2025-09-19 15:00:00.000000', 0, 18, 11);
INSERT INTO `forum_comment` VALUES (24, '频闪这个很多人忽略，确实要看。', '2025-09-20 10:10:00.000000', 1, 19, 12);
INSERT INTO `forum_comment` VALUES (25, '色温 4000K 我也觉得最舒服。', '2025-09-20 11:00:00.000000', 0, 19, 14);
INSERT INTO `forum_comment` VALUES (26, '最好面交试穿，不然偏码很难判断。', '2025-09-20 14:10:00.000000', 0, 20, 13);
INSERT INTO `forum_comment` VALUES (27, '鞋垫磨损也能看出真实尺码偏差。', '2025-09-20 15:20:00.000000', 0, 20, 15);
INSERT INTO `forum_comment` VALUES (28, '支持规范发帖，论坛氛围靠大家。', '2025-09-21 10:00:00.000000', 2, 21, 2);
INSERT INTO `forum_comment` VALUES (29, '建议交易帖加图片/自提地点更清晰。', '2025-09-21 11:10:00.000000', 1, 21, 3);
INSERT INTO `forum_comment` VALUES (30, '可以加个“禁止发布外链/诈骗”的提醒。', '2025-09-21 12:00:00.000000', 0, 21, 4);

-- ----------------------------
-- Table structure for forum_post
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(4000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `last_reply_at` datetime(6) NULL DEFAULT NULL,
  `like_count` bigint NOT NULL,
  `reply_count` bigint NOT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `user_id` bigint NOT NULL,
  `view_count` bigint NOT NULL,
  `product_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of forum_post
-- ----------------------------
INSERT INTO `forum_post` VALUES (7, '<p>学弟学妹可以优先考虑二手：台灯/收纳箱/插排/风扇，预算更友好。</p>', '2025-09-16 09:10:00.000000', '2025-09-16 11:30:00.000000', 2, 2, '开学季必备清单（低预算版）', 7, 36, NULL);
INSERT INTO `forum_post` VALUES (8, '<p>求购全套资料或真题，最好带解析，价格可谈。</p>', '2025-09-16 10:05:00.000000', '2025-09-16 12:20:00.000000', 1, 2, '求购：高数/线代/概率论资料', 8, 52, NULL);
INSERT INTO `forum_post` VALUES (9, '<p>面交验机建议：看外观、看电池健康、查序列号、跑分看温控，最好当场还原一次。</p>', '2025-09-16 13:40:00.000000', '2025-09-16 18:10:00.000000', 6, 3, '二手手机验机避坑指南', 9, 88, 3004);
INSERT INTO `forum_post` VALUES (10, '<p>看网课、做笔记、PDF 标注都很方便，但别为了“看起来学习”而买。</p>', '2025-09-17 09:30:00.000000', '2025-09-17 10:45:00.000000', 4, 2, 'iPad 适合哪些学习场景？', 10, 75, 3003);
INSERT INTO `forum_post` VALUES (11, '<p>看宿舍电费/功率限制，别一开就跳闸。二手买注意制冷和噪音。</p>', '2025-09-17 11:20:00.000000', '2025-09-17 12:55:00.000000', 3, 2, '宿舍小冰箱值不值得买', 11, 61, 3007);
INSERT INTO `forum_post` VALUES (12, '<p>新手别追高磅数，先练动作。二手拍看框有没有裂纹、线床是否松。</p>', '2025-09-17 14:10:00.000000', '2025-09-17 16:10:00.000000', 2, 2, '羽毛球拍选购建议（新手向）', 12, 44, 3010);
INSERT INTO `forum_post` VALUES (13, '<p>建议：公共场所、人多的地方、最好有同学陪同，交易前核验物品。</p>', '2025-09-18 09:00:00.000000', '2025-09-18 12:05:00.000000', 9, 3, '自提交易如何更安全？', 13, 97, NULL);
INSERT INTO `forum_post` VALUES (14, '<p>两周内练习用，押金可谈，最好同校自提。</p>', '2025-09-18 10:10:00.000000', '2025-09-18 11:00:00.000000', 0, 1, '求租：短期键盘/吉他（两周）', 14, 26, 3011);
INSERT INTO `forum_post` VALUES (15, '<p>透明/可叠放/带卡扣更好用。二手收纳箱注意是否发黄、有无异味。</p>', '2025-09-18 15:30:00.000000', '2025-09-18 18:00:00.000000', 1, 2, '收纳箱怎么选更实用', 15, 39, 3014);
INSERT INTO `forum_post` VALUES (16, '<p>标题建议：核心品类 + 关键规格 + 成色 + 交易方式，例如“iPad 128G 九成新 自提”。</p>', '2025-09-19 09:20:00.000000', '2025-09-19 13:10:00.000000', 12, 3, '出闲置时标题怎么写更容易卖', 2, 120, NULL);
INSERT INTO `forum_post` VALUES (17, '<p>建议录制打包视频，贵重物品走保价，沟通清楚瑕疵并拍照留存。</p>', '2025-09-19 10:05:00.000000', '2025-09-19 12:40:00.000000', 7, 2, '邮寄交易注意事项', 3, 84, NULL);
INSERT INTO `forum_post` VALUES (18, '<p>优先本校同城自提，想要干净无大量笔记版本。</p>', '2025-09-19 14:20:00.000000', '2025-09-19 15:00:00.000000', 1, 1, '求购：计算机网络/操作系统教材', 4, 33, NULL);
INSERT INTO `forum_post` VALUES (19, '<p>看色温、显色指数、频闪。宿舍用建议 4000K 左右更舒服。</p>', '2025-09-20 09:10:00.000000', '2025-09-20 11:00:00.000000', 5, 2, '台灯护眼参数怎么理解', 5, 58, 3008);
INSERT INTO `forum_post` VALUES (20, '<p>问清楚鞋楦、穿着脚型、是否顶脚。最好面交试穿。</p>', '2025-09-20 13:10:00.000000', '2025-09-20 15:20:00.000000', 2, 2, '球鞋二手如何判断是否偏码', 6, 47, 3009);
INSERT INTO `forum_post` VALUES (21, '<p>请勿发布违规信息；交易帖请说明价格/成色/交易方式；有纠纷请先沟通再反馈。</p>', '2025-09-21 09:00:00.000000', '2025-09-21 12:00:00.000000', 34, 4, '论坛发帖规范（建议）', 1, 211, NULL);

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of message
-- ----------------------------
INSERT INTO `message` VALUES (1, 1, 'ORDER', '订单状态更新', '您的订单 ORD202509010001 状态已更新为“待发货”。', 0, '2025-09-01 13:00:00');
INSERT INTO `message` VALUES (2, 2, 'SYSTEM', '系统通知', '欢迎加入校园二手平台。', 1, '2025-09-02 09:00:00');
INSERT INTO `message` VALUES (3, 3, 'AUDIT', '审核提醒', '您发布的商品已提交审核。', 0, '2025-09-03 09:00:00');
INSERT INTO `message` VALUES (4, 4, 'CHAT', '新消息', '有人咨询了您的商品。', 0, '2025-09-04 09:00:00');
INSERT INTO `message` VALUES (5, 5, 'ORDER', '订单提醒', '您有一笔新订单待处理。', 0, '2025-09-05 09:00:00');
INSERT INTO `message` VALUES (6, 6, 'SYSTEM', '系统通知', '平台规则已更新，请查看。', 1, '2025-09-06 09:00:00');
INSERT INTO `message` VALUES (7, 7, 'ORDER', '订单状态更新', '订单已取消。', 1, '2025-09-07 09:00:00');
INSERT INTO `message` VALUES (8, 8, 'SYSTEM', '系统通知', '账号资料完善可提升成交率。', 0, '2025-09-08 09:00:00');
INSERT INTO `message` VALUES (9, 9, 'CHAT', '新消息', '买家想约时间自提。', 0, '2025-09-09 09:00:00');
INSERT INTO `message` VALUES (10, 10, 'AUDIT', '审核结果', '商品审核通过，已上架。', 1, '2025-09-10 09:00:00');
INSERT INTO `message` VALUES (11, 11, 'SYSTEM', '系统通知', '本周活动：开学季专场。', 0, '2025-09-11 09:00:00');
INSERT INTO `message` VALUES (12, 12, 'ORDER', '订单提醒', '请尽快支付订单。', 0, '2025-09-12 09:00:00');
INSERT INTO `message` VALUES (13, 13, 'CHAT', '新消息', '对方已回复您的咨询。', 1, '2025-09-13 09:00:00');
INSERT INTO `message` VALUES (14, 14, 'SYSTEM', '系统通知', '您已获得新优惠券。', 0, '2025-09-14 09:00:00');
INSERT INTO `message` VALUES (15, 15, 'SYSTEM', '账号状态', '账号已被禁用，如有疑问请联系客服。', 0, '2025-09-15 09:00:00');

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SYSTEM',
  `pinned` tinyint(1) NOT NULL DEFAULT 0,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '平台公告：严禁交易违规物品', '平台严禁发布或交易任何违法违规物品，一经发现将下架并封禁账号。', 'RULE', 1, 1, '2025-09-10 09:00:00');
INSERT INTO `notice` VALUES (2, '交易安全提示', '建议在公共区域自提交易，核验物品后再确认转账，谨防诈骗。', 'RULE', 1, 1, '2025-09-11 09:00:00');
INSERT INTO `notice` VALUES (3, '开学季活动：数码专场', '开学季数码专场开启，精选好物推荐，欢迎参与发布与选购。', 'ACTIVITY', 0, 1, '2025-09-12 09:00:00');
INSERT INTO `notice` VALUES (4, '系统维护通知', '平台将于本周末 02:00-03:00 进行维护，期间可能短暂不可用。', 'SYSTEM', 0, 1, '2025-09-13 09:00:00');
INSERT INTO `notice` VALUES (5, '发货建议（邮寄）', '邮寄交易建议录制打包视频并使用保价服务，贵重物品建议走顺丰。', 'RULE', 0, 1, '2025-09-14 09:00:00');
INSERT INTO `notice` VALUES (6, '毕业季清仓活动', '毕业季清仓活动上线，欢迎同学们发布闲置，转给有需要的人。', 'ACTIVITY', 0, 1, '2025-09-15 09:00:00');
INSERT INTO `notice` VALUES (7, '平台功能更新：聊天咨询', '已上线在线咨询功能，建议在平台内沟通留痕，交易更安全。', 'SYSTEM', 0, 1, '2025-09-16 09:00:00');
INSERT INTO `notice` VALUES (8, '关于恶意砍价与辱骂的处理', '对于恶意骚扰、辱骂等行为平台将依据规则限制功能或封禁账号。', 'RULE', 0, 1, '2025-09-17 09:00:00');
INSERT INTO `notice` VALUES (9, '校园自提地点建议', '自提建议选择图书馆门口、食堂门口等人流量较大的公共区域。', 'RULE', 0, 1, '2025-09-18 09:00:00');
INSERT INTO `notice` VALUES (10, '账号安全提醒', '请勿将账号密码告知他人，定期修改密码，避免账号被盗。', 'SYSTEM', 0, 1, '2025-09-19 09:00:00');
INSERT INTO `notice` VALUES (11, '平台公告：虚假描述将被下架', '商品描述需真实准确，刻意隐瞒瑕疵或虚假描述将被下架处理。', 'RULE', 0, 1, '2025-09-20 09:00:00');
INSERT INTO `notice` VALUES (12, '活动：教材资料专区', '教材资料专区开启，欢迎发布闲置教材/资料，文明交流。', 'ACTIVITY', 0, 1, '2025-09-21 09:00:00');
INSERT INTO `notice` VALUES (13, '系统公告：图片上传优化', '图片上传体验已优化，建议使用清晰照片展示成色与细节。', 'SYSTEM', 0, 1, '2025-09-22 09:00:00');
INSERT INTO `notice` VALUES (14, '售后与纠纷处理建议', '发生纠纷请先友好协商，保留聊天记录与证据，必要时反馈平台。', 'RULE', 0, 1, '2025-09-23 09:00:00');
INSERT INTO `notice` VALUES (15, '平台公告：发布规范', '发布商品请填写清晰标题、价格、成色、交易方式与自提地点。', 'RULE', 0, 1, '2025-09-24 09:00:00');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `operator` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `result` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_log_time`(`created_at` ASC) USING BTREE,
  INDEX `idx_operation_log_type`(`type` ASC) USING BTREE,
  INDEX `idx_operation_log_operator`(`operator` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, '示例用户(13800000000)', '内容管控', 'POST /api/admin/content/products/3015/audit', '127.0.0.1', '成功', '2026-03-04 14:17:12');

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `price` int NOT NULL,
  `product_cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `product_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `quantity` int NOT NULL,
  `total_amount` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------

-- ----------------------------
-- Table structure for order_log
-- ----------------------------
DROP TABLE IF EXISTS `order_log`;
CREATE TABLE `order_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_log
-- ----------------------------
INSERT INTO `order_log` VALUES (1, 9001, 'CREATED', '订单已创建', '2025-09-01 12:00:00');
INSERT INTO `order_log` VALUES (2, 9002, 'CREATED', '订单已创建', '2025-09-02 12:00:00');
INSERT INTO `order_log` VALUES (3, 9003, 'CREATED', '订单已创建', '2025-09-03 12:00:00');
INSERT INTO `order_log` VALUES (4, 9004, 'CREATED', '订单已创建', '2025-09-04 12:00:00');
INSERT INTO `order_log` VALUES (5, 9005, 'CREATED', '订单已创建', '2025-09-05 12:00:00');
INSERT INTO `order_log` VALUES (6, 9006, 'CREATED', '订单已创建', '2025-09-06 12:00:00');
INSERT INTO `order_log` VALUES (7, 9007, 'CREATED', '订单已创建', '2025-09-07 12:00:00');
INSERT INTO `order_log` VALUES (8, 9008, 'CREATED', '订单已创建', '2025-09-08 12:00:00');
INSERT INTO `order_log` VALUES (9, 9009, 'CREATED', '订单已创建', '2025-09-09 12:00:00');
INSERT INTO `order_log` VALUES (10, 9010, 'CREATED', '订单已创建', '2025-09-10 12:00:00');
INSERT INTO `order_log` VALUES (11, 9011, 'CREATED', '订单已创建', '2025-09-11 12:00:00');
INSERT INTO `order_log` VALUES (12, 9012, 'CREATED', '订单已创建', '2025-09-12 12:00:00');
INSERT INTO `order_log` VALUES (13, 9013, 'CREATED', '订单已创建', '2025-09-13 12:00:00');
INSERT INTO `order_log` VALUES (14, 9014, 'CREATED', '订单已创建', '2025-09-14 12:00:00');
INSERT INTO `order_log` VALUES (15, 9015, 'CREATED', '订单已创建', '2025-09-15 12:00:00');
INSERT INTO `order_log` VALUES (16, 9002, 'WAIT_SHIP', '待发货', '2025-09-02 12:05:00');
INSERT INTO `order_log` VALUES (17, 9003, 'SHIPPED', '已发货', '2025-09-03 12:10:00');
INSERT INTO `order_log` VALUES (18, 9004, 'COMPLETED', '已完成', '2025-09-04 18:00:00');
INSERT INTO `order_log` VALUES (19, 9006, 'CANCELLED', '订单已取消', '2025-09-06 12:30:00');
INSERT INTO `order_log` VALUES (20, 9008, 'AFTER_SALE', '已申请售后', '2025-09-08 12:40:00');
INSERT INTO `order_log` VALUES (21, 9012, 'SHIPPED', '已发货', '2025-09-12 12:20:00');
INSERT INTO `order_log` VALUES (22, 9016, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 09:06:14');
INSERT INTO `order_log` VALUES (23, 9016, 'AFTER_SALE', '已申请售后', '2026-03-04 09:06:30');
INSERT INTO `order_log` VALUES (24, 9016, 'AFTER_SALE', '已申请售后', '2026-03-04 09:06:33');
INSERT INTO `order_log` VALUES (25, 9016, 'AFTER_SALE', '已申请售后', '2026-03-04 09:07:33');
INSERT INTO `order_log` VALUES (26, 9016, 'AFTER_SALE', '已申请售后', '2026-03-04 09:24:07');
INSERT INTO `order_log` VALUES (27, 9017, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 09:44:53');
INSERT INTO `order_log` VALUES (28, 9018, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 10:24:07');
INSERT INTO `order_log` VALUES (29, 9018, 'WAIT_SHIP', '已支付（支付方式：微信支付），待卖家发货', '2026-03-04 10:24:21');
INSERT INTO `order_log` VALUES (30, 9019, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 10:31:18');
INSERT INTO `order_log` VALUES (31, 9020, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 10:31:59');
INSERT INTO `order_log` VALUES (32, 9021, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 10:31:59');
INSERT INTO `order_log` VALUES (33, 9022, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 13:45:01');
INSERT INTO `order_log` VALUES (34, 9023, 'WAIT_PAY', '订单已创建，待支付', '2026-03-04 13:45:02');

-- ----------------------------
-- Table structure for order_main
-- ----------------------------
DROP TABLE IF EXISTS `order_main`;
CREATE TABLE `order_main`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `buyer_id` bigint NOT NULL,
  `seller_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` int NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `total_amount` int NOT NULL,
  `delivery_type_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `logistics_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `paid_at` datetime(6) NULL DEFAULT NULL,
  `payment_method` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9024 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_main
-- ----------------------------
INSERT INTO `order_main` VALUES (9001, 'ORD202509010001', 7, 2, 3001, '高等数学上册教材', '/upload/product_3001.jpg', 25, 1, 25, '自提', '示例地址1', NULL, 'WAIT_PAY', '2025-09-01 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9002, 'ORD202509010002', 8, 2, 3002, '高等数学上册教材 + 习题册', '/upload/product_3002.jpg', 39, 1, 39, '自提', '示例地址2', NULL, 'WAIT_SHIP', '2025-09-02 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9003, 'ORD202509010003', 9, 3, 3003, 'iPad 2020 128G（含原装笔）', '/upload/product_3003.jpg', 1850, 1, 1850, '自提', '示例地址3', NULL, 'SHIPPED', '2025-09-03 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9004, 'ORD202509010004', 10, 3, 3004, '二手手机：旗舰机 256G', '/upload/product_3004.jpg', 1299, 1, 1299, '邮寄', '示例地址4', 'SF123', 'COMPLETED', '2025-09-04 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9005, 'ORD202509010005', 11, 4, 3005, '轻薄本 13 寸 i5', '/upload/product_3005.jpg', 2399, 1, 2399, '邮寄', '示例地址5', NULL, 'WAIT_PAY', '2025-09-05 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9006, 'ORD202509010006', 12, 4, 3006, '平板电脑 64G', '/upload/product_3006.jpg', 699, 1, 699, '自提', '示例地址6', NULL, 'CANCELLED', '2025-09-06 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9007, 'ORD202509010007', 13, 5, 3007, '宿舍小冰箱', '/upload/product_3007.jpg', 260, 1, 260, '自提', '示例地址7', NULL, 'WAIT_SHIP', '2025-09-07 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9008, 'ORD202509010008', 14, 5, 3008, '宿舍台灯', '/upload/product_3008.jpg', 35, 2, 70, '自提', '示例地址8', NULL, 'AFTER_SALE', '2025-09-08 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9009, 'ORD202509010009', 7, 6, 3009, '球鞋 42 码', '/upload/product_3009.jpg', 199, 1, 199, '自提', '示例地址9', NULL, 'WAIT_PAY', '2025-09-09 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9010, 'ORD202509010010', 8, 6, 3010, '羽毛球拍（双拍）', '/upload/product_3010.jpg', 120, 1, 120, '自提', '示例地址10', NULL, 'WAIT_SHIP', '2025-09-10 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9011, 'ORD202509010011', 9, 2, 3011, '吉他入门民谣', '/upload/product_3011.jpg', 399, 1, 399, '自提', '示例地址11', NULL, 'COMPLETED', '2025-09-11 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9012, 'ORD202509010012', 10, 3, 3012, '相机三脚架', '/upload/product_3012.jpg', 88, 1, 88, '邮寄', '示例地址12', 'YT001', 'SHIPPED', '2025-09-12 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9013, 'ORD202509010013', 11, 4, 3013, '考研政治资料', '/upload/product_3013.jpg', 49, 1, 49, '邮寄', '示例地址13', NULL, 'WAIT_SHIP', '2025-09-13 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9014, 'ORD202509010014', 12, 5, 3014, '收纳箱 3 个', '/upload/product_3014.jpg', 45, 1, 45, '自提', '示例地址14', NULL, 'WAIT_PAY', '2025-09-14 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9015, 'ORD202509010015', 13, 6, 3015, '其它闲置：小风扇', '/upload/product_3015.jpg', 18, 1, 18, '自提', '示例地址15', NULL, 'WAIT_PAY', '2025-09-15 12:00:00', NULL, NULL);
INSERT INTO `order_main` VALUES (9016, 'O1772586374432b97aba9c', 1, 6, 3015, '其它闲置：小风扇', '/upload/product_3015.jpg', 18, 1, 18, '自提', NULL, NULL, 'AFTER_SALE', '2026-03-04 09:06:14', NULL, NULL);
INSERT INTO `order_main` VALUES (9017, 'O1772588692510b3cea8b1', 1, 2, 3001, '高等数学上册教材', '/upload/product_3001.jpg', 25, 1, 25, '自提', NULL, NULL, 'WAIT_PAY', '2026-03-04 09:44:53', NULL, NULL);
INSERT INTO `order_main` VALUES (9018, 'O1772591047052f08cee20', 1, 6, 3015, '其它闲置：小风扇', '/upload/product_3015.jpg', 18, 1, 18, '自提', NULL, NULL, 'WAIT_SHIP', '2026-03-04 10:24:07', '2026-03-04 10:24:21.401417', '微信支付');
INSERT INTO `order_main` VALUES (9019, 'O1772591477847947c9f8b', 1, 5, 3014, '收纳箱 3 个', '/upload/product_3014.jpg', 45, 1, 45, '自提', NULL, NULL, 'WAIT_PAY', '2026-03-04 10:31:18', NULL, NULL);
INSERT INTO `order_main` VALUES (9020, 'O17725915189027bb5ea02', 1, 4, 3013, '考研政治资料', '/upload/product_3013.jpg', 49, 1, 49, '邮寄', NULL, NULL, 'WAIT_PAY', '2026-03-04 10:31:59', NULL, NULL);
INSERT INTO `order_main` VALUES (9021, 'O177259151918147e1db1b', 1, 2, 3001, '高等数学上册教材', '/upload/product_3001.jpg', 25, 1, 25, '自提', NULL, NULL, 'WAIT_PAY', '2026-03-04 10:31:59', NULL, NULL);
INSERT INTO `order_main` VALUES (9022, 'O1772603101103a3a8cace', 1, 5, 3014, '收纳箱 3 个', '/upload/product_3014.jpg', 45, 1, 45, '自提', NULL, NULL, 'WAIT_PAY', '2026-03-04 13:45:01', NULL, NULL);
INSERT INTO `order_main` VALUES (9023, 'O1772603101702554c70b9', 1, 6, 3015, '其它闲置：小风扇', '/upload/product_3015.jpg', 18, 1, 18, '自提', NULL, NULL, 'WAIT_PAY', '2026-03-04 13:45:02', NULL, NULL);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seller_id` bigint NOT NULL,
  `campus_id` bigint NULL DEFAULT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` int NOT NULL,
  `condition_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `delivery_type_text` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `free_shipping` tinyint(1) NOT NULL DEFAULT 0,
  `pickup_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `description_html` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `view_count` int NOT NULL DEFAULT 0,
  `favorite_count` int NOT NULL DEFAULT 0,
  `consult_count` int NOT NULL DEFAULT 0,
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'DRAFT',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3016 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (3001, 2, 1, 2, '高等数学上册教材', '/upload/product_3001.jpg', 25, '九成新', '自提', 0, NULL, '<p>教材无笔记，支持当面验货。</p>', 123, 15, 2, NULL, 'PUBLISHED', '2025-09-01 12:00:00');
INSERT INTO `product` VALUES (3002, 2, 1, 2, '高等数学上册教材 + 习题册', '/upload/product_3002.jpg', 39, '九成新', '自提 / 可邮寄', 0, '东区图书馆门口', '<p>正版教材+习题册，轻微折角。</p>', 88, 9, 1, NULL, 'PUBLISHED', '2025-09-01 10:00:00');
INSERT INTO `product` VALUES (3003, 3, 2, 9, 'iPad 2020 128G（含原装笔）', '/upload/product_3003.jpg', 1850, '八成新', '自提', 0, '西区宿舍门口', '<p>电池健康良好，支持验机。</p>', 66, 20, 5, NULL, 'PUBLISHED', '2025-09-02 10:00:00');
INSERT INTO `product` VALUES (3004, 3, 2, 8, '二手手机：旗舰机 256G', '/upload/product_3004.jpg', 1299, '八成新', '自提 / 可邮寄', 1, NULL, '<p>无拆无修，配件齐全。</p>', 45, 12, 3, NULL, 'PUBLISHED', '2025-09-03 10:00:00');
INSERT INTO `product` VALUES (3005, 4, 3, 9, '轻薄本 13 寸 i5', '/upload/product_3005.jpg', 2399, '七成新', '邮寄', 1, NULL, '<p>适合上课，风扇声音小。</p>', 31, 8, 2, NULL, 'PUBLISHED', '2025-09-04 10:00:00');
INSERT INTO `product` VALUES (3006, 4, 3, 9, '平板电脑 64G', '/upload/product_3006.jpg', 699, '九成新', '自提', 0, '南区食堂门口', '<p>屏幕无划痕。</p>', 22, 6, 1, NULL, 'PUBLISHED', '2025-09-05 10:00:00');
INSERT INTO `product` VALUES (3007, 5, 4, 13, '宿舍小冰箱', '/upload/product_3007.jpg', 260, '八成新', '自提', 0, '北区门口', '<p>制冷正常，低噪音。</p>', 77, 14, 2, NULL, 'PUBLISHED', '2025-09-06 10:00:00');
INSERT INTO `product` VALUES (3008, 5, 4, 13, '宿舍台灯', '/upload/product_3008.jpg', 35, '九成新', '自提', 0, NULL, '<p>三档调光，夹子稳。</p>', 90, 18, 4, NULL, 'PUBLISHED', '2025-09-07 10:00:00');
INSERT INTO `product` VALUES (3009, 6, 5, 15, '球鞋 42 码', '/upload/product_3009.jpg', 199, '七成新', '自提', 0, NULL, '<p>适合日常训练。</p>', 55, 10, 2, NULL, 'PUBLISHED', '2025-09-08 10:00:00');
INSERT INTO `product` VALUES (3010, 6, 5, 15, '羽毛球拍（双拍）', '/upload/product_3010.jpg', 120, '八成新', '自提', 0, '体育馆门口', '<p>线床完整，握把已更换。</p>', 41, 7, 1, NULL, 'PUBLISHED', '2025-09-09 10:00:00');
INSERT INTO `product` VALUES (3011, 2, 6, 6, '吉他入门民谣', '/upload/product_3011.jpg', 399, '八成新', '自提', 0, NULL, '<p>含琴包+变调夹。</p>', 38, 9, 1, NULL, 'PUBLISHED', '2025-09-10 10:00:00');
INSERT INTO `product` VALUES (3012, 3, 7, 10, '相机三脚架', '/upload/product_3012.jpg', 88, '九成新', '自提 / 可邮寄', 1, NULL, '<p>轻便稳定。</p>', 29, 5, 1, NULL, 'PUBLISHED', '2025-09-11 10:00:00');
INSERT INTO `product` VALUES (3013, 4, 8, 12, '考研政治资料', '/upload/product_3013.jpg', 49, '九成新', '邮寄', 1, NULL, '<p>基本全新，附笔记。</p>', 101, 22, 6, NULL, 'PUBLISHED', '2025-09-12 10:00:00');
INSERT INTO `product` VALUES (3014, 5, 9, 14, '收纳箱 3 个', '/upload/product_3014.jpg', 45, '九成新', '自提', 0, NULL, '<p>透明可叠放。</p>', 19, 3, 0, NULL, 'PUBLISHED', '2025-09-13 10:00:00');
INSERT INTO `product` VALUES (3015, 6, 10, 7, '其它闲置：小风扇', '/upload/product_3015.jpg', 18, '八成新', '自提', 0, NULL, '<p>桌面小风扇，三档风力。</p>', 12, 2, 0, NULL, 'PUBLISHED', '2025-09-14 10:00:00');

-- ----------------------------
-- Table structure for product_media
-- ----------------------------
DROP TABLE IF EXISTS `product_media`;
CREATE TABLE `product_media`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sort_order` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_media
-- ----------------------------
INSERT INTO `product_media` VALUES (1, 3001, 'IMAGE', '/upload/product_3001.jpg', 1);
INSERT INTO `product_media` VALUES (2, 3002, 'IMAGE', '/upload/product_3002.jpg', 1);
INSERT INTO `product_media` VALUES (3, 3003, 'IMAGE', '/upload/product_3003.jpg', 1);
INSERT INTO `product_media` VALUES (4, 3004, 'IMAGE', '/upload/product_3004.jpg', 1);
INSERT INTO `product_media` VALUES (5, 3005, 'IMAGE', '/upload/product_3005.jpg', 1);
INSERT INTO `product_media` VALUES (6, 3006, 'IMAGE', '/upload/product_3006.jpg', 1);
INSERT INTO `product_media` VALUES (7, 3007, 'IMAGE', '/upload/product_3007.jpg', 1);
INSERT INTO `product_media` VALUES (8, 3008, 'IMAGE', '/upload/product_3008.jpg', 1);
INSERT INTO `product_media` VALUES (9, 3009, 'IMAGE', '/upload/product_3009.jpg', 1);
INSERT INTO `product_media` VALUES (10, 3010, 'IMAGE', '/upload/product_3010.jpg', 1);
INSERT INTO `product_media` VALUES (11, 3011, 'IMAGE', '/upload/product_3011.jpg', 1);
INSERT INTO `product_media` VALUES (12, 3012, 'IMAGE', '/upload/product_3012.jpg', 1);
INSERT INTO `product_media` VALUES (13, 3013, 'IMAGE', '/upload/product_3013.jpg', 1);
INSERT INTO `product_media` VALUES (14, 3014, 'IMAGE', '/upload/product_3014.jpg', 1);
INSERT INTO `product_media` VALUES (15, 3015, 'IMAGE', '/upload/product_3015.jpg', 1);

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL,
  `copyright_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `homepage_notice` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password_rule` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `platform_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `service_contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `trade_rules` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `violation_rules` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, '© 2026 校园二手交易平台', '欢迎使用校园二手交易平台，文明交易，安全第一。', NULL, NULL, '校园二手交易平台', 'QQ：123456 / 微信：campus-helper', '禁止发布违法违规商品，交易前请确认对方身份。', '发现违规将视情节禁用账号。');

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `nickname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `student_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `campus_id` bigint NULL DEFAULT NULL,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NORMAL',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_account
-- ----------------------------
INSERT INTO `user_account` VALUES (1, 'user001', '123456', '示例用户', '20230001', '13800000000', 1, '/upload/6d84782916f549d8abb7a59362427c44.webp', 'ADMIN', 'NORMAL', '2025-09-01 09:00:00');
INSERT INTO `user_account` VALUES (2, 'seller002', '123456', '理学院·学长', '20220001', '13800000001', 1, NULL, 'SELLER', 'NORMAL', '2025-08-20 10:00:00');
INSERT INTO `user_account` VALUES (3, 'seller003', '123456', '信息工程·学姐', '20220002', '13800000002', 2, NULL, 'SELLER', 'NORMAL', '2025-08-22 11:00:00');
INSERT INTO `user_account` VALUES (4, 'seller004', '123456', '外语学院·学长', '20220003', '13800000003', 3, NULL, 'SELLER', 'NORMAL', '2025-08-25 12:00:00');
INSERT INTO `user_account` VALUES (5, 'seller005', '123456', '经管学院·学姐', '20220004', '13800000004', 4, NULL, 'SELLER', 'NORMAL', '2025-08-28 13:00:00');
INSERT INTO `user_account` VALUES (6, 'seller006', '123456', '体育学院·学长', '20220005', '13800000005', 5, NULL, 'SELLER', 'NORMAL', '2025-08-30 14:00:00');
INSERT INTO `user_account` VALUES (7, 'user007', '123456', '用户007', '20230007', '13800000007', 6, NULL, 'USER', 'NORMAL', '2025-09-02 09:10:00');
INSERT INTO `user_account` VALUES (8, 'user008', '123456', '用户008', '20230008', '13800000008', 7, NULL, 'USER', 'NORMAL', '2025-09-03 09:20:00');
INSERT INTO `user_account` VALUES (9, 'user009', '123456', '用户009', '20230009', '13800000009', 8, NULL, 'USER', 'NORMAL', '2025-09-04 09:30:00');
INSERT INTO `user_account` VALUES (10, 'user010', '123456', '用户010', '20230010', '13800000010', 9, NULL, 'USER', 'NORMAL', '2025-09-05 09:40:00');
INSERT INTO `user_account` VALUES (11, 'user011', '123456', '用户011', '20230011', '13800000011', 10, NULL, 'USER', 'NORMAL', '2025-09-06 09:50:00');
INSERT INTO `user_account` VALUES (12, 'user012', '123456', '用户012', '20230012', '13800000012', 11, NULL, 'USER', 'NORMAL', '2025-09-07 10:00:00');
INSERT INTO `user_account` VALUES (13, 'user013', '123456', '用户013', '20230013', '13800000013', 12, NULL, 'USER', 'NORMAL', '2025-09-08 10:10:00');
INSERT INTO `user_account` VALUES (14, 'user014', '123456', '用户014', '20230014', '13800000014', 13, NULL, 'USER', 'NORMAL', '2025-09-09 10:20:00');
INSERT INTO `user_account` VALUES (15, 'user015', '123456', '用户015', '20230015', '13800000015', 14, NULL, 'USER', 'DISABLED', '2025-09-10 10:30:00');

SET FOREIGN_KEY_CHECKS = 1;
