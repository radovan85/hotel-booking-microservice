<template>
    <div class="container" style="font-family: Rajdhani, sans-serif; color: #12044F; font-weight: 700;margin-bottom: 100px;margin-top: 125px;">
        <div class="text-center text-uppercase mb-4">
            <h3 v-html="'Room update Form'"></h3>
            <hr class="w-25 mx-auto">
        </div>
        
        <div class="row">
            <div class="col-md-6 offset-md-3">
                <form class="mt-3"  id="roomForm">
                    
                    <div class="mb-3">
                        <label for="roomNumber" class="form-label" v-html="'Room Number'"></label>
                        <input 
                            type="text" 
                            class="form-control" 
                            id="roomNumber"
                            name="roomNumber"  
                            placeholder="Enter Room Number"
                            :value="currentRoom.roomNumber"
                            @keydown="validationService.validateNumber($event)"
                        />
                        <span class="text-danger" id="roomNumberError" 
                            v-html="'Please provide room number!'"
                            style="visibility: hidden">
                        </span>
                    </div>
                    
                    <div class="mb-5">
                        <label for="roomCategory" class="form-label" v-html="'Room Category'"></label>
                        <select class="form-select" id="roomCategory" name="roomCategoryId">
                            <option :value="''" v-html="'Please Select'"></option>
                            <option v-for="tempCategory in categoryList" :key="tempCategory.roomCategoryId"
                                :value="tempCategory.roomCategoryId" 
                                v-html="tempCategory.name"
                                :selected="tempCategory.roomCategoryId === currentRoom.roomCategoryId">
                            </option>
                        </select>
                        <span class="text-danger" id="roomCategoryError" 
                            v-html="'Please provide room category!'"
                            style="visibility: hidden">
                        </span>
                    </div>

                    <div class="text-center">
                        <button type="submit" class="btn btn-info" 
                            v-html="'Update'" >
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import Room from '@/classes/Room';
import RoomCategoryService from '@/services/RoomCategoryService';
import RoomService from '@/services/RoomService';
import ValidationService from '@/services/ValidationService';
import axios from 'axios';
import { defineComponent } from 'vue';
import { useRoute } from 'vue-router';


export default defineComponent({

  data(){
    return {
        roomService: new RoomService,
        categoryService: new RoomCategoryService,
        validationService: new ValidationService,
        categoryList: [] as any [],
        currentRoom: new Room,
        route: useRoute()
    };
  },

  methods: {

    listAllCategories():Promise<any> {
        return new Promise(() => {
            this.categoryService.collectAllCategories()
            .then((response) => {
                this.categoryList = response.data;
            })
        })
    },

    getRoomDetails(roomId:any):Promise<any> {
        return new Promise(() => {
            this.roomService.getRoomDetails(roomId)
            .then((response) => {
                this.currentRoom = response.data;
            })
        })
    }
  },

  mounted() {
    
    var form = document.getElementById(`roomForm`) as HTMLFormElement;

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateRoom()) {
        await axios.put(`${this.roomService.getTargetUrl()}/${this.currentRoom.roomId}`, {
          roomNumber: Number(serializedData[`roomNumber`]),
          roomCategoryId: Number(serializedData[`roomCategoryId`]),
        })
          .then(() => {
            this.roomService.redirectAllRooms();
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

  created(){
    this.listAllCategories();
    this.getRoomDetails(this.route.params.roomId);
  }

});

</script>