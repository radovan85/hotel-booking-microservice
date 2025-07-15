export default class ValidationService {
  validateRoomCategory() {
    var name = (<HTMLInputElement>document.getElementById(`name`)).value.trim();
    var price = (<HTMLInputElement>(
      document.getElementById(`price`)
    )).value.trim();
    var wc = (<HTMLInputElement>document.getElementById(`wc`)).value.trim();
    var wifi = (<HTMLInputElement>document.getElementById("wifi")).value.trim();
    var tv = (<HTMLInputElement>document.getElementById("tv")).value.trim();
    var bar = (<HTMLInputElement>document.getElementById("bar")).value.trim();

    var nameError = document.getElementById("nameError");
    var priceError = document.getElementById("priceError");
    var wcError = document.getElementById("wcError");
    var wifiError = document.getElementById("wifiError");
    var tvError = document.getElementById("tvError");
    var barError = document.getElementById("barError");

    var priceNum = Number(price);
    var returnValue = true;

    if (nameError) {
      if (name === "" || name.length > 30) {
        nameError.style.visibility = "visible";
        returnValue = false;
      } else {
        nameError.style.visibility = "hidden";
      }
    }

    if (priceError) {
      if (price === "" || priceNum <= 0) {
        priceError.style.visibility = "visible";
        returnValue = false;
      } else {
        priceError.style.visibility = "hidden";
      }
    }

    if (wcError) {
      if (wc === "") {
        wcError.style.visibility = "visible";
        returnValue = false;
      } else {
        wcError.style.visibility = "hidden";
      }
    }

    if (wifiError) {
      if (wifi === "") {
        wifiError.style.visibility = "visible";
        returnValue = false;
      } else {
        wifiError.style.visibility = "hidden";
      }
    }

    if (tvError) {
      if (tv === "") {
        tvError.style.visibility = "visible";
        returnValue = false;
      } else {
        tvError.style.visibility = "hidden";
      }
    }

    if (barError) {
      if (bar === "") {
        barError.style.visibility = "visible";
        returnValue = false;
      } else {
        barError.style.visibility = "hidden";
      }
    }

    return returnValue;
  }

  validateRoom() {
    var roomNumber = (<HTMLInputElement>(
      document.getElementById("roomNumber")
    )).value.trim();
    var roomCategory = (<HTMLInputElement>(
      document.getElementById("roomCategory")
    )).value.trim();

    var roomNumberError = document.getElementById("roomNumberError");
    var roomCategoryError = document.getElementById("roomCategoryError");

    var roomNumberNum = Number(roomNumber);
    var returnValue = true;

    if (roomNumberError) {
      if (roomNumber === "" || roomNumberNum <= 0) {
        roomNumberError.style.visibility = "visible";
        returnValue = false;
      } else {
        roomNumberError.style.visibility = "hidden";
      }
    }

    if (roomCategoryError) {
      if (roomCategory === "" || roomCategory.length > 30) {
        roomCategoryError.style.visibility = "visible";
        returnValue = false;
      } else {
        roomCategoryError.style.visibility = "hidden";
      }
    }

    return returnValue;
  }

  validateRegForm() {
    var firstName = (<HTMLInputElement>(
      document.getElementById("firstName")
    )).value.trim();
    var lastName = (<HTMLInputElement>(
      document.getElementById("lastName")
    )).value.trim();
    var email = (<HTMLInputElement>(
      document.getElementById("email")
    )).value.trim();
    var password = (<HTMLInputElement>(
      document.getElementById("password")
    )).value.trim();
    var confirmpass = (<HTMLInputElement>(
      document.getElementById("confirmpass")
    )).value.trim();
    var idNumber = (<HTMLInputElement>(
      document.getElementById("idNumber")
    )).value.trim();
    var phoneNumber = (<HTMLInputElement>(
      document.getElementById("phoneNumber")
    )).value.trim();

    var firstNameError = document.getElementById("firstNameError");
    var lastNameError = document.getElementById("lastNameError");
    var emailError = document.getElementById("emailError");
    var passwordError = document.getElementById("passwordError");
    var idNumberError = document.getElementById("idNumberError");
    var phoneNumberError = document.getElementById("phoneNumberError");

    var regEmail = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/g;
    var returnValue = true;

    if (password !== confirmpass) {
      returnValue = false;
      alert(`Password not match!`);
    }

    if (firstNameError) {
      if (firstName === "" || firstName.length > 30) {
        firstNameError.style.visibility = "visible";
        returnValue = false;
      } else {
        firstNameError.style.visibility = "hidden";
      }
    }

    if (lastNameError) {
      if (lastName === "" || lastName.length > 30) {
        lastNameError.style.visibility = "visible";
        returnValue = false;
      } else {
        lastNameError.style.visibility = "hidden";
      }
    }

    if (emailError) {
      if (email === "" || !regEmail.test(email) || email.length > 40) {
        emailError.style.visibility = "visible";
        returnValue = false;
      } else {
        emailError.style.visibility = "hidden";
      }
    }

    if (passwordError) {
      if (password.length < 6 || password.length > 30) {
        passwordError.style.visibility = "visible";
        returnValue = false;
      } else {
        passwordError.style.visibility = "hidden";
      }
    }

    if (idNumberError) {
      if (idNumber.length < 6 || idNumber.length > 12) {
        idNumberError.style.visibility = "visible";
        returnValue = false;
      } else {
        idNumberError.style.visibility = "hidden";
      }
    }

    if (phoneNumberError) {
      if (phoneNumber.length < 9 || phoneNumber.length > 15) {
        phoneNumberError.style.visibility = "visible";
        returnValue = false;
      } else {
        phoneNumberError.style.visibility = "hidden";
      }
    }

    return returnValue;
  }

  validateRoomNumber() {
    var roomId = (<HTMLInputElement>(
      document.getElementById("roomId")
    )).value.trim();
    var roomIdError = document.getElementById("roomIdError");
    var returnValue = true;

    if (roomIdError) {
      if (roomId === ``) {
        returnValue = false;
        roomIdError.style.visibility = `visible`;
      } else {
        roomIdError.style.visibility = `hidden`;
      }
    }
    return returnValue;
  }

  validateNumber(event: KeyboardEvent): void {
    var allowedKeys = ["Backspace", "ArrowLeft", "ArrowRight", "Tab"];

    if (!/^[\d.]$/.test(event.key) && !allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  }
}
