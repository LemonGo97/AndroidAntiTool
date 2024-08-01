import { basePermissions } from '@/settings'
import { asyncRoutes as asyncPermissions } from "@/router/async-routes.js";
import api from '@/api'

// export async function getUserInfo() {
//   const res = await api.getUser()
//   const { id, username, profile, roles, currentRole } = res.data || {}
//   return {
//     id,
//     username,
//     avatar: profile?.avatar,
//     nickName: profile?.nickName,
//     gender: profile?.gender,
//     address: profile?.address,
//     email: profile?.email,
//     roles,
//     currentRole,
//   }
// }
export async function getUserInfo() {
  const data = {
    "id": 1,
    "username": "admin",
    "enable": true,
    "createTime": "2023-11-18T08:18:59.150Z",
    "updateTime": "2023-11-18T08:18:59.150Z",
    "profile": {
      "id": 1,
      "nickName": "Admin",
      "gender": null,
      "avatar": "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif?imageView2/1/w/80/h/80",
      "address": null,
      "email": null,
      "userId": 1
    },
    "roles": [
      {
        "id": 1,
        "code": "SUPER_ADMIN",
        "name": "超级管理员",
        "enable": true
      },
      {
        "id": 2,
        "code": "ROLE_QA",
        "name": "质检员",
        "enable": true
      }
    ],
    "currentRole": {
      "id": 1,
      "code": "SUPER_ADMIN",
      "name": "超级管理员",
      "enable": true
    }
  }
  const { id, username, profile, roles, currentRole } = data || {}
  return {
    id,
    username,
    avatar: profile?.avatar,
    nickName: profile?.nickName,
    gender: profile?.gender,
    address: profile?.address,
    email: profile?.email,
    roles,
    currentRole,
  }
}

export async function getPermissions() {
  // let asyncPermissions = []
  // try {
  //   const res = await api.getRolePermissions()
  //   asyncPermissions = res?.data || []
  // }
  // catch (error) {
  //   console.error(error)
  // }
  return basePermissions.concat(asyncPermissions)
}
