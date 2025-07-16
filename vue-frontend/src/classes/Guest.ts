export default class Guest {
    
    private _guestId?: number;
    private _phoneNumber?: string;
    private _idNumber?: number;
    private _userId?: number;
  
    get guestId(): number | undefined {
      return this._guestId;
    }
  
    set guestId(value: number | undefined) {
      this._guestId = value;
    }
  
    get phoneNumber(): string | undefined {
      return this._phoneNumber;
    }
  
    set phoneNumber(value: string | undefined) {
      this._phoneNumber = value;
    }
  
    get idNumber(): number | undefined {
      return this._idNumber;
    }
  
    set idNumber(value: number | undefined) {
      this._idNumber = value;
    }
  
    get userId(): number | undefined {
      return this._userId;
    }
  
    set userId(value: number | undefined) {
      this._userId = value;
    }
  }
  