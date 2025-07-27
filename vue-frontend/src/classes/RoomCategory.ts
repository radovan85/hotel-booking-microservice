export default class RoomCategory {
    
    private _roomCategoryId?: number;
    private _name?: string;
    private _price?: number;
    private _wifi?: number;
    private _wc?: number;
    private _tv?: number;
    private _bar?: number;
    private _roomsIds?: number[];
  
    get roomCategoryId(): number | undefined {
      return this._roomCategoryId;
    }
  
    set roomCategoryId(value: number | undefined) {
      this._roomCategoryId = value;
    }
  
    get name(): string | undefined {
      return this._name;
    }
  
    set name(value: string | undefined) {
      this._name = value;
    }
  
    get price(): number | undefined {
      return this._price;
    }
  
    set price(value: number | undefined) {
      this._price = value;
    }
  
    get wifi(): number | undefined {
      return this._wifi;
    }
  
    set wifi(value: number | undefined) {
      this._wifi = value;
    }
  
    get wc(): number | undefined {
      return this._wc;
    }
  
    set wc(value: number | undefined) {
      this._wc = value;
    }
  
    get tv(): number | undefined {
      return this._tv;
    }
  
    set tv(value: number | undefined) {
      this._tv = value;
    }
  
    get bar(): number | undefined {
      return this._bar;
    }
  
    set bar(value: number | undefined) {
      this._bar = value;
    }
  
    get roomsIds(): number[] | undefined {
      return this._roomsIds;
    }
  
    set roomsIds(value: number[] | undefined) {
      this._roomsIds = value;
    }
  }
  