<template>
    <div class="container"
		style="font-family: Rajdhani, sans-serif;color: #12044F;font-weight: 700;margin-top: 100px;margin-bottom: 100px;">
	
		<div class="text-center">
        	<h1 v-html="'Registration Completed'"></h1>
    	</div>
        
        
    	<div class="text-center mt-5" >
    		<router-link class="btn btn-primary border-dark" 
            	v-html="'Login Page'"
                to="/login">
        	</router-link>
    	</div>
	</div>
</template>

