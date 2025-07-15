import router from "@/router";
import axios from "axios";

export default class RoomService {
  private targetUrl = `http://localhost:8090/api/rooms`;

  collectAllRooms(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  getRoomDetails(roomId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${roomId}`);
  }

  async deleteRoom(roomId:any):Promise<any> {
    return await axios.delete(`${this.targetUrl}/${roomId}`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }

  redirectAllRooms() {
    router.push(`/rooms`);
  }
}
