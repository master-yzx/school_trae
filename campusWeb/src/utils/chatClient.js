import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.min.js';
import { useAuthStore } from '../stores/auth';

let client = null;
let connected = false;
const subscribers = new Set();

export function connectChat() {
  if (client && connected) return;

  const auth = useAuthStore();
  if (!auth.accessToken || !auth.user?.id) return;

  client = new Client({
    webSocketFactory: () => new SockJS('/ws'),
    connectHeaders: {
      Authorization: `Bearer ${auth.accessToken}`,
    },
    debug: () => {},
    onConnect() {
      connected = true;
      const userId = auth.user.id;
      client.subscribe(`/queue/chat/${userId}`, (msg) => {
        try {
          const body = JSON.parse(msg.body);
          subscribers.forEach((fn) => fn(body));
        } catch (e) {
          // ignore parse error
        }
      });
    },
    onStompError() {
      connected = false;
    },
    onWebSocketClose() {
      connected = false;
    },
  });

  client.activate();
}

export function disconnectChat() {
  if (client) {
    client.deactivate();
    client = null;
    connected = false;
    subscribers.clear();
  }
}

export function sendChatMessage(sessionId, content) {
  if (!client || !connected || !sessionId || !content || !content.trim()) return;
  client.publish({
    destination: '/app/chat.send',
    body: JSON.stringify({ sessionId, content }),
  });
}

export function onChatMessage(handler) {
  subscribers.add(handler);
  return () => subscribers.delete(handler);
}

