<template>
  <div v-if="isReady" class="container-fluid text-center" style="font-family: Rajdhani, sans-serif; font-weight: 700; margin-bottom: 100px; margin-top: 125px;">
    <div class="row justify-content-center">
      <div class="col-lg-8 text-center">
        <section>
          <div class="text-center mb-5">
            <img :src="require('@/assets/images/hotel_1.jpeg')" />
          </div>
          <h2 class="h1-responsive font-weight-bold text-center mb-4">Woods</h2>
          <p class="text-muted text-center w-75 mx-auto mb-5">
            The hotel is located among trees, overlooking the mountains, where nature meets modern style.
          </p>

          <div class="row g-4">
            <div class="col-md-12 mb-4">
              <div class="card bg-dark text-white">
                <img src="https://mdbootstrap.com/img/Photos/Others/img%20%2832%29.jpg" class="card-img" alt="Hotel" />
                <div class="card-img-overlay d-flex align-items-center justify-content-center">
                  <div class="text-center">
                    <h2 class="card-title font-weight-bold">Hotel</h2>
                    <p class="card-text">
                      Stay with us in <span style="font-family: 'Inria Serif', serif">Woods</span>. Elegance, luxurious and contemporary design, as well as numerous amenities for guests - these are just some of the advantages of our unique hotel.
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-md-6 mb-4">
              <div class="card bg-dark text-white">
                <img src="https://mdbootstrap.com/img/Photos/Others/img%20%2848%29.jpg" class="card-img" alt="Restaurant" />
                <div class="card-img-overlay d-flex align-items-center justify-content-center">
                  <div class="text-center">
                    <h3 class="card-title font-weight-bold">Restaurant</h3>
                    <p class="card-text">
                      Not only a hotel, but also a restaurant famous for its delicious cuisine and a large banquet (wedding) room...
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <div class="col-md-6 mb-4">
              <div class="card bg-dark text-white">
                <img src="https://mdbootstrap.com/img/Photos/Others/img%20%2848%29.jpg" class="card-img" alt="Events" />
                <div class="card-img-overlay d-flex align-items-center justify-content-center">
                  <div class="text-center">
                    <h3 class="card-title font-weight-bold">Occasional Events</h3>
                    <p class="card-text">
                      We specialize in organizing conferences and special events. Our stylishly furnished rooms, elegant conference rooms...
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>

      <div class="col-lg-2 text-center">
        <div class="mb-3">
          <label class="text-darkred" v-html="'Hello ' + authUser.firstName + ' ' + authUser.lastName"></label>
        </div>
        <div v-if="hasAuthorityUser">
          <router-link class="btn btn-success" to="/account">Your Account</router-link>
        </div>
      </div>
    </div>
  </div>

  <div v-else class="text-center mt-5">
    <div class="spinner-border text-dark" role="status">
      <span class="visually-hidden">Loading...</span>
    </div>
  </div>
</template>

<script lang="ts">
import User from "@/classes/User";
import { AuthService } from "@/services/AuthService";
import UserService from "@/services/UserService";
import { defineComponent } from "vue";

export default defineComponent({
  name: "HomeView",

  data() {
    return {
      isReady: false,
      authUser: new User,
      userService: new UserService,
      hasAuthorityUser: false,
      authService: new AuthService
    };
  },

  methods: {
    getAuthUser(): Promise<void> {
      return this.userService.getAuthUser()
        .then((response) => {
          this.authUser = response.data;
        });
    }
  },

  created() {
    Promise.all([
      this.getAuthUser()
    ])
    .then(() => {
      this.hasAuthorityUser = this.authService.isUser();
      this.isReady = true;
    })
    .catch((err) => {
      console.error("Auth greška:", err);
    });
  }
});
</script>
