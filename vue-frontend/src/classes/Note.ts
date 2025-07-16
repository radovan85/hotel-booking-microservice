export default class Note {
    private _noteId?: number;
    private _subject?: string;
    private _text?: string;
    private _createTimeStr?: string;
  
    // NoteId
    public get noteId(): number | undefined {
      return this._noteId;
    }
  
    public set noteId(value: number | undefined) {
      this._noteId = value;
    }
  
    // Subject
    public get subject(): string | undefined {
      return this._subject;
    }
  
    public set subject(value: string | undefined) {
      this._subject = value;
    }
  
    // Text
    public get text(): string | undefined {
      return this._text;
    }
  
    public set text(value: string | undefined) {
      this._text = value;
    }
  
    // CreateTimeStr
    public get createTimeStr(): string | undefined {
      return this._createTimeStr;
    }
  
    public set createTimeStr(value: string | undefined) {
      this._createTimeStr = value;
    }
  }
  