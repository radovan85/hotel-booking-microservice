import Guest from "./Guest"
import User from "./User"

export default class RegistrationForm {
  private _user?: User
  private _guest?: Guest

  get user(): User | undefined {
    return this._user
  }

  set user(value: User | undefined) {
    this._user = value
  }

  get guest(): Guest | undefined {
    return this._guest
  }

  set guest(value: Guest | undefined) {
    this._guest = value
  }
}
