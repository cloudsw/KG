import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from "@/views/pk/PkIndexView"
import RanklistIndexView from "@/views/ranklist/RanklistIndexView"
import RecordIndexView from "@/views/record/RecordIndexView"
import UserBotIndexView from "@/views/user/bot/UserBotIndexView"
import NotFound from "@/views/error/NotFound"

const routes = [
  {
    path: "/",      //进行重定向，当访问根目录时  重定向到/pk/路径
    name: "home",
    redirect: "/pk/"
  },
  {
    path: "/pk/",   //访问/pk/路径时，显示PkIndexView页面
    name: "pk_index",
    component: PkIndexView
  },
  {
    path: "/record/",
    name: "record_index",
    component: RanklistIndexView
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RecordIndexView
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView
  },
  {
    path: "/404/",
    name: "not_found_index",
    component: NotFound
  },
  {
    path: "/:catchAll(.*)",   //当访问不存在的路径时，跳转到/404/路径
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
