export const asyncRoutes = [
  {
    name: "基础功能",
    title: "基础功能",
    code: "Base",
    type: "MENU",
    icon: "i-fe:grid",
    show: true,
    enable: true,
    order: 0,
    children: [
      {
        name: "基础组件",
        title: "基础组件",
        code: "BaseComponents",
        type: "MENU",
        path: "/base/components",
        icon: "i-me:awesome",
        component: "/src/views/base/index.vue",
        show: true,
        enable: true,
        order: 1
      },
      {
        name: "Unocss",
        code: "Unocss",
        type: "MENU",
        path: "/base/unocss",
        icon: "i-me:awesome",
        component: "/src/views/base/unocss.vue",
        show: true,
        enable: true,
        order: 2
      },
      {
        name: "keepAlive",
        code: "keepAlive",
        type: "MENU",
        path: "/base/keep-alive",
        icon: "i-me:awesome",
        component: "/src/views/base/keep-alive.vue",
        show: true,
        enable: true,
        order: 3
      },
      {
        name: "图标 Icon",
        code: "icon",
        type: "MENU",
        path: "/base/icon",
        icon: "i-fe:feather",
        component: "/src/views/base/unocss-icon.vue",
        layout: "",
        show: true,
        enable: true,
        order: 0
      },
      {
        name: "MeModal",
        code: "TestModal",
        type: "MENU",
        path: "/testModal",
        icon: "i-me:dialog",
        component: "/src/views/base/test-modal.vue",
        show: true,
        enable: true,
        order: 5
      }
    ]
  }
]
