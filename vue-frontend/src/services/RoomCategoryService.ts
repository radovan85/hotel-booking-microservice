import router from "@/router";
import axios from "axios";

export default class RoomCategoryService {
  private targetUrl = `http://localhost:8090/api/categories`;

  collectAllCategories(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  async deleteCategory(categoryId: any): Promise<any> {
    return await axios.delete(`${this.targetUrl}/${categoryId}`);
  }

  getCategoryDetails(categoryId:any):Promise<any> {
    return axios.get(`${this.targetUrl}/${categoryId}`);
  }

  redirectAllCategories() {
    router.push(`/categories`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }
}
