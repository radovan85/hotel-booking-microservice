import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '@/views/Login.vue'
import { UnidentifiedGuard } from '@/guards/UnidentifiedGuard'
import { AuthGuard } from '@/guards/AuthGuard'
import GuestListView from '@/views/guests/GuestListView.vue'
import { AdminGuard } from '@/guards/AdminGuard'
import GuestDetailsView from '@/views/guests/GuestDetailsView.vue'
import RoomCategoryListView from '@/views/categories/RoomCategoryListView.vue'
import RoomCategoryFormView from '@/views/categories/RoomCategoryFormView.vue'
import RoomCategoryUpdateFormView from '@/views/categories/RoomCategoryUpdateFormView.vue'
import RoomCategoryDetailsView from '@/views/categories/RoomCategoryDetailsView.vue'
import RoomListView from '@/views/rooms/RoomListView.vue'
import RoomFormView from '@/views/rooms/RoomFormView.vue'
import RoomDetailsView from '@/views/rooms/RoomDetailsView.vue'
import RoomUpdateFormView from '@/views/rooms/RoomUpdateFormView.vue'
import RegistrationView from '@/views/guests/RegistrationView.vue'
import RegistrationCompletedView from '@/views/guests/RegistrationCompletedView.vue'
import ContactView from '@/views/ContactView.vue'
import AccountView from '@/views/guests/AccountView.vue'
import { UserGuard } from '@/guards/UserGuard'
import UserReservationsView from '@/views/reservations/UserReservationsView.vue'
import BookReservationFormView from '@/views/reservations/BookReservationFormView.vue'
import ReservationBookedView from '@/views/reservations/ReservationBookedView.vue'
import NoteListView from '@/views/notes/NoteListView.vue'
import NoteListTodayView from '@/views/notes/NoteListTodayView.vue'
import NoteDetailsView from '@/views/notes/NoteDetailsView.vue'
import AdminReservationsView from '@/views/reservations/AdminReservationsView.vue'
import ReservationDetailsView from '@/views/reservations/ReservationDetailsView.vue'
import SwitchRoomFormView from '@/views/reservations/SwitchRoomFormView.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/home',
    component: HomeView,
    beforeEnter: AuthGuard
  },
  
  {
    path: "/login",
    component: Login,
    beforeEnter: UnidentifiedGuard
  },

  {
    path: `/guests`,
    component: GuestListView,
    beforeEnter: AdminGuard
  },

  {
    path: `/guests/guestDetails/:guestId`,
    component: GuestDetailsView,
    beforeEnter: AdminGuard
  },

  {
    path: `/categories`,
    component: RoomCategoryListView,
    beforeEnter: AdminGuard
  },

  {
    path: `/categories/addCategory`,
    component: RoomCategoryFormView,
    beforeEnter: AdminGuard
  },

  {
    path: `/categories/updateCategory/:categoryId`,
    component: RoomCategoryUpdateFormView,
    beforeEnter: AdminGuard
  },

  {
    path: `/categories/categoryDetails/:categoryId`,
    component: RoomCategoryDetailsView,
    beforeEnter: AdminGuard
  },

  {
    path: `/rooms`,
    component: RoomListView,
    beforeEnter: AdminGuard
  },

  {
    path: `/rooms/addRoom`,
    component: RoomFormView,
    beforeEnter: AdminGuard
  },

  {
    path: `/rooms/roomDetails/:roomId`,
    component: RoomDetailsView,
    beforeEnter: AdminGuard
  },

  {
    path: `/rooms/updateRoom/:roomId`,
    component: RoomUpdateFormView,
    beforeEnter: AdminGuard
  },

  {
    path: `/register`,
    component: RegistrationView,
    beforeEnter: UnidentifiedGuard
  },

  {
    path: `/registerCompleted`,
    component: RegistrationCompletedView,
    beforeEnter: UnidentifiedGuard
  },

  {
    path: `/contact`,
    component: ContactView
  },

  {
    path: `/account`,
    component: AccountView,
    beforeEnter: UserGuard
  },

  {
    path: `/reservations/me`,
    component: UserReservationsView,
    beforeEnter: UserGuard
  },

  {
    path: `/reservations/book`,
    component: BookReservationFormView,
    beforeEnter: UserGuard
  },

  {
    path: `/reservations/booked`,
    component: ReservationBookedView,
    beforeEnter: UserGuard
  },

  {
    path: `/notes`,
    component: NoteListView,
    beforeEnter: AdminGuard
  },

  {
    path: `/notes/today`,
    component: NoteListTodayView,
    beforeEnter: AdminGuard
  },

  {
    path: `/notes/noteDetails/:id`,
    component: NoteDetailsView,
    beforeEnter: AdminGuard
  },

  {
    path: `/reservations`,
    component: AdminReservationsView,
    beforeEnter: AdminGuard
  },

  {
    path: `/reservations/details/:id`,
    component: ReservationDetailsView,
    beforeEnter: AdminGuard
  },

  {
    path: `/reservations/switchRoom/:reservationId`,
    component: SwitchRoomFormView,
    beforeEnter: AdminGuard
  },


  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
  
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
