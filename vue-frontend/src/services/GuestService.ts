import router from "@/router";
import axios from "axios";

export default class GuestService {

    private targetUrl = `http://localhost:8090/api/guests`;

    collectAllGuests():Promise<any>{
        return axios.get(`${this.targetUrl}`);
    }

    getGuestDetails(guestId:any):Promise<any>{
        return axios.get(`${this.targetUrl}/${guestId}`);
    }

    async deleteGuest(guestId:any):Promise<any>{
        return await axios.delete(`${this.targetUrl}/${guestId}`);
    }

    getCurrentGuest():Promise<any>{
        return axios.get(`${this.targetUrl}/me`);
    }

    redirectAllGuests(){
        router.push(`/guests`);
    }

    getTargetUrl(){
        return this.targetUrl;
    }


}