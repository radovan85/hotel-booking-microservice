export default class Reservation {
    private _reservationId?: number;
    private _roomId?: number;
    private _guestId?: number;
    private _checkInDateStr?: string;
    private _checkOutDateStr?: string;
    private _createTimeStr?: string;
    private _updateTimeStr?: string;
    private _price?: number;
    private _numberOfNights?: number;
  
    // Geteri
    public get reservationId(): number | undefined {
      return this._reservationId;
    }
  
    public get roomId(): number | undefined {
      return this._roomId;
    }
  
    public get guestId(): number | undefined {
      return this._guestId;
    }
  
    public get checkInDateStr(): string | undefined {
      return this._checkInDateStr;
    }
  
    public get checkOutDateStr(): string | undefined {
      return this._checkOutDateStr;
    }
  
    public get createTimeStr(): string | undefined {
      return this._createTimeStr;
    }
  
    public get updateTimeStr(): string | undefined {
      return this._updateTimeStr;
    }
  
    public get price(): number | undefined {
      return this._price;
    }
  
    public get numberOfNights(): number | undefined {
      return this._numberOfNights;
    }
  
    // Seteri
    public set reservationId(value: number | undefined) {
      this._reservationId = value;
    }
  
    public set roomId(value: number | undefined) {
      this._roomId = value;
    }
  
    public set guestId(value: number | undefined) {
      this._guestId = value;
    }
  
    public set checkInDateStr(value: string | undefined) {
      this._checkInDateStr = value;
    }
  
    public set checkOutDateStr(value: string | undefined) {
      this._checkOutDateStr = value;
    }
  
    public set createTimeStr(value: string | undefined) {
      this._createTimeStr = value;
    }
  
    public set updateTimeStr(value: string | undefined) {
      this._updateTimeStr = value;
    }
  
    public set price(value: number | undefined) {
      this._price = value;
    }
  
    public set numberOfNights(value: number | undefined) {
      this._numberOfNights = value;
    }
  
    
    public get canBeCanceled(): boolean {
      if (!this._checkInDateStr) return false;
  
      const checkIn = new Date(this._checkInDateStr + ' UTC');
      const now = new Date();
      const cancelDeadline = new Date(now.getTime() + 24 * 60 * 60 * 1000);
  
      return cancelDeadline < checkIn;
    }
  }
  