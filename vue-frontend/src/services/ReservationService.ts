import router from "@/router";
import axios from "axios";

export default class ReservationService {

    private targetUrl = `http://localhost:8090/api/reservations`;

    getTargetUrl(){
        return this.targetUrl;
    }

    redirectReservationBooked(){
        router.push(`/reservations/booked`);
    }

    collectAllReservations():Promise<any>{
        return axios.get(`${this.targetUrl}`);
    }

    collectAllExpiredReservations():Promise<any>{
        return axios.get(`${this.targetUrl}/expired`);
    }

    collectAllActiveReservations():Promise<any>{
        return axios.get(`${this.targetUrl}/active`);
    }

    collectMyReservations():Promise<any> {
        return axios.get(`${this.targetUrl}/me`);
    }

    async cancelReservation(reservationId:any):Promise<any>{
        return await axios.delete(`${this.targetUrl}/cancel/${reservationId}`);
    }

    async deleteReservation(reservationId:any):Promise<any>{
        return await axios.delete(`${this.targetUrl}/${reservationId}`);
    }

    getReservationDetails(reservationId:any):Promise<any>{
        return axios.get(`${this.targetUrl}/${reservationId}`);
    }

    collectAlternativeRooms(reservationId:any):Promise<any> {
        return axios.get(`${this.targetUrl}/findAlternatives/${reservationId}`);
    }
}