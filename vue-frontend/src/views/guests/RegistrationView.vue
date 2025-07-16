<template>
  <div
    class="container"
    style="
      margin-top: 120px;
      margin-bottom: 100px;
      font-family: Rajdhani, sans-serif;
      color: #12044f;
      font-weight: 700;
    "
  >
    <div class="text-center text-uppercase py-3">
      <h3 v-html="'Registration Form'"></h3>
      <hr />
    </div>

    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card shadow-sm">
          <div class="card-body">
            <form id="registrationForm">
              <div class="mb-3">
                <label class="form-label" v-html="'First Name'"></label>
                <input
                  type="text"
                  class="form-control"
                  id="firstName"
                  name="firstName"
                  placeholder="Enter First Name"
                />
                <span
                  class="text-danger small"
                  id="firstNameError"
                  v-html="'Not empty!Max 30 letters allowed!'"
                  style="visibility: hidden"
                >
                </span>
              </div>

              <div class="mb-3">
                <label class="form-label" v-html="'Last Name'"></label>
                <input
                  type="text"
                  class="form-control"
                  id="lastName"
                  name="lastName"
                  placeholder="Enter Last Name"
                />
                <span
                  class="text-danger small"
                  id="lastNameError"
                  v-html="'Not empty!Max 30 letters allowed!'"
                  style="visibility: hidden"
                >
                </span>
              </div>

              <div class="mb-3">
                <label class="form-label" v-html="'Email'"></label>
                <input
                  type="email"
                  class="form-control"
                  id="email"
                  name="email"
                  placeholder="Enter Email Address"
                />
                <span
                  class="text-danger small"
                  id="emailError"
                  v-html="'Please provide valid email! Max 40 letters allowed!'"
                  style="visibility: hidden"
                >
                </span>
              </div>

              <div class="mb-3">
                <label class="form-label" v-html="'Password'"></label>
                <input
                  type="password"
                  class="form-control"
                  id="password"
                  name="password"
                  placeholder="Enter Password"
                />
                <span
                  class="text-danger small"
                  id="passwordError"
                  v-html="'Please provide password! Min 6 max 30 letters!'"
                  style="visibility: hidden"
                >
                </span>
              </div>

              <div class="mb-3">
                <label class="form-label" v-html="'Confirm Password'"></label>
                <input
                  type="password"
                  class="form-control"
                  id="confirmpass"
                  placeholder="Confirm Password"
                />
              </div>

              <div class="mb-3">
                <label class="form-label" v-html="'Id Number'"></label>
                <input
                  type="text"
                  class="form-control"
                  id="idNumber"
                  name="idNumber"
                  placeholder="Enter Id number"
                  @keydown="validationService.validateNumber($event)"
                />
                <span
                  class="text-danger small"
                  id="idNumberError"
                  v-html="'Minimum 6 Max 12 Letters!'"
                  style="visibility: hidden"
                >
                </span>
              </div>

              <div class="mb-4">
                <label class="form-label" v-html="'Phone'"></label>
                <input
                  type="text"
                  class="form-control"
                  id="phoneNumber"
                  name="phoneNumber"
                  placeholder="Enter Phone Number"
                  @keydown="validationService.validateNumber($event)"
                />
                <span
                  class="text-danger small"
                  id="phoneNumberError"
                  v-html="'Minimum 9 Max 15 Letters!'"
                  style="visibility: hidden"
                >
                </span>
              </div>

              <div class="text-center">
                <button
                  type="submit"
                  class="btn btn-info text-uppercase px-4"
                  v-html="'Register'"
                ></button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import GuestService from "@/services/GuestService";
import ValidationService from "@/services/ValidationService";
import axios from "axios";
import { defineComponent } from "vue";
import { useRouter } from "vue-router";

export default defineComponent({
  
  data() {
    return {
      guestService: new GuestService(),
      validationService: new ValidationService(),
      router: useRouter()
    };
  },

  mounted() {
    var form = document.getElementById(`registrationForm`) as HTMLFormElement;

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateRegForm()) {
        await axios
          .post(`${this.guestService.getTargetUrl()}/register`, {
            user: {
              firstName: serializedData[`firstName`],
              lastName: serializedData[`lastName`],
              email: serializedData[`email`],
              password: serializedData[`password`],
            },

            guest: {
              phoneNumber: serializedData[`phoneNumber`],
              idNumber: Number(serializedData[`idNumber`]),
            },
          })
          .then(() => {
            this.router.push(`/registerCompleted`);
          })

          .catch((error) => {
            if (error.response.status === 409) {
              alert(error.response.data);
            } else {
              console.log(error);
            }
          });
      }
    });
  },
});
</script>
