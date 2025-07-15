export default class Room {
    
    private _roomId?: number;
    private _roomNumber?: number;
    private _price?: number;
    private _roomCategoryId?: number;
  
    get roomId(): number | undefined {
      return this._roomId;
    }
  
    set roomId(value: number | undefined) {
      this._roomId = value;
    }
  
    get roomNumber(): number | undefined {
      return this._roomNumber;
    }
  
    set roomNumber(value: number | undefined) {
      this._roomNumber = value;
    }
  
    get price(): number | undefined {
      return this._price;
    }
  
    set price(value: number | undefined) {
      this._price = value;
    }
  
    get roomCategoryId(): number | undefined {
      return this._roomCategoryId;
    }
  
    set roomCategoryId(value: number | undefined) {
      this._roomCategoryId = value;
    }
  }
  