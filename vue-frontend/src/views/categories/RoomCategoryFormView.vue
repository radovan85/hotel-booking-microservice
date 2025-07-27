<template>
    <div class="container" style="font-family: Rajdhani, sans-serif; color: #12044F; font-weight: 700;margin-bottom: 100px;margin-top: 120px;">
		<div class="text-center text-uppercase pb-3"> 
        	<h3>Category Form</h3>
            <hr class="mx-auto" style="width: 50px; border-top: 3px solid #12044F;">
       	</div>
            
        <div class="row justify-content-center">
            <div class="col-lg-6 col-md-8">
            	<div class="card shadow-sm p-4">
					<form  id="roomCategoryForm">
					
						<div class="mb-3">
                        	<label class="form-label" v-html="'Name'"></label>
                          	<input 
                          		type="text" 
                          		class="form-control" 
                          		id="name"
                          		name="name"  
                          		placeholder="Enter Category Name"
                          	/> 
                          	<span class="text-danger" id="nameError" 
            					v-html="'Not empty! Max 30 letters allowed!'" 
            					style="visibility: hidden">
            				</span>      
                    	</div>
                    	
                    	<div class="mb-3">
                        	<label class="form-label" v-html="'Price'"></label>
                          	<input 
                          		type="text" 
                          		class="form-control" 
                          		id="price"
                          		name="price"  
                          		placeholder="Enter Price"
                          		@keydown="validationService.validateNumber($event)"
                          	/> 
                          	<span class="text-danger" id="priceError" 
            					v-html="'Please provide valid price!'" 
            					style="visibility: hidden">
            				</span>      
                    	</div>
                    	
                    	<div class="mb-3">
                        	<label class="form-label" v-html="'Wc'"></label>
                          	<select class="form-select" id="wc"  name="wc">
           						<option :value="''" v-html="'Please Select'"></option>
            					<option :value="'0'" v-html="'No'"></option>
            					<option :value="'1'" v-html="'Yes'"></option>
        					</select>
                          	<span class="text-danger" id="wcError"
                          		v-html="'Please select one of the options!'" 
            					style="visibility: hidden">
            				</span>    
                    	</div>
                    	
                    	<div class="mb-3">
                        	<label class="form-label" v-html="'Wi-fi'"></label>
                          	<select class="form-select" id="wifi" name="wifi">
           						<option :value="''" v-html="'Please Select'"></option>
            					<option :value="'0'" v-html="'No'"></option>
            					<option :value="'1'" v-html="'Yes'"></option>
        					</select>
            				<span class="text-danger" id="wifiError" 
                            v-html="'Please select one of the options!'" 
            					style="visibility: hidden">
            				</span>   
                    	</div>
            
            			<div class="mb-3">
                        	<label class="form-label" v-html="'Tv'"></label>
                          	<select class="form-select" id="tv" name="tv">
           						<option :value="''" v-html="'Please Select'"></option>
            					<option :value="'0'" v-html="'No'"></option>
            					<option :value="'1'" v-html="'Yes'"></option>
        					</select>
            				<span class="text-danger" id="tvError"
                            v-html="'Please select one of the options!'" 
            					style="visibility: hidden">
            				</span>  
                    	</div>
                    	
                    	<div class="mb-3">
                        	<label class="form-label" v-html="'Bar'"></label>
                          	<select class="form-select" id="bar" name="bar">
           						<option :value="''" v-html="'Please Select'"></option>
            					<option :value="'0'" v-html="'No'"></option>
            					<option :value="'1'" v-html="'Yes'"></option>
        					</select>
            				<span class="text-danger" id="barError" 
                            v-html="'Please select one of the options!'" 
            					style="visibility: hidden;">
            				</span>
                    	</div>
             
            			<div class="text-center">
    						<button type="submit" class="btn btn-info px-4" 
                            v-html="'Submit'" >
    						</button>
    					</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts">
import RoomCategoryService from '@/services/RoomCategoryService';
import ValidationService from '@/services/ValidationService';
import axios from 'axios';
import { defineComponent } from 'vue';


export default defineComponent({
  
  data() {
    return {
        categoryService: new RoomCategoryService,
        validationService: new ValidationService
    };  
  },

  mounted(){
    
    var form = document.getElementById(`roomCategoryForm`) as HTMLFormElement;

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateRoomCategory()) {
        await axios.post(`${this.categoryService.getTargetUrl()}`, {
          name: serializedData[`name`],
          price: Number(serializedData[`price`]),
          wc: Number(serializedData[`wc`]),
          wifi: Number(serializedData[`wifi`]),
          tv: Number(serializedData[`tv`]),
          bar: Number(serializedData[`bar`]),

        })
          .then(() => {
            this.categoryService.redirectAllCategories();
          })

          .catch((error) => {
            console.log(error);
          });
      }
    });

  }

  

});
</script>