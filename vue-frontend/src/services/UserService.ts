import axios from "axios";

export default class UserService {

    private targetUrl = `http://localhost:8090/api/auth`;

    collectAllUsers():Promise<any> {
        return axios.get(`${this.targetUrl}/users`);
    }

    getAuthUser():Promise<any>{
        return axios.get(`${this.targetUrl}/me`);
    }
}