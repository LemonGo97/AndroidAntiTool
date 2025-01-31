/**********************************
 * @FilePath: index.js
 * @Author: Ronnie Zhang
 * @LastEditor: Ronnie Zhang
 * @LastEditTime: 2023/12/04 22:46:28
 * @Email: zclzone@outlook.com
 * Copyright © 2023 Ronnie Zhang(大脸怪) | https://isme.top
 **********************************/

import axios from 'axios'
import {setupInterceptors} from './interceptors'

export function createAxios(options = {}) {
  const defaultOptions = {
    baseURL: import.meta.env.VITE_AXIOS_BASE_URL,
    timeout: 12000,
  }
  const service = axios.create({
    ...defaultOptions,
    ...options,
  })
  setupInterceptors(service)
  return service
}

export const request = createAxios()

export const mockRequest = createAxios({
  baseURL: '/mock-api',
})

export function createEventSource(uri, messageEventListener = () => {}, openEventListener = () => {}, errorEventListener = () => {}) {
  const url = `${import.meta.env.VITE_AXIOS_BASE_URL}${uri}`;
  const eventSource = new EventSource(url);
  eventSource.addEventListener("open", openEventListener);
  eventSource.addEventListener("error", errorEventListener);
  eventSource.addEventListener("message", messageEventListener);
  return eventSource;
}

export function createWebSocket(uri){
  const url = `${import.meta.env.VITE_WEBSOCKET_BASE_URL}${uri}`;
  return new WebSocket(url);
}
