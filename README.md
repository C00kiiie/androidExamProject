# Overview

I've created a banking app,  

## Before using

The app requires a internet connection.

If you want to use an existing user:
           email:   test@test.dk
    password:  123456
    

## Usage

The app has an register system, that you can use to register a new user.
That user can then be used to login with. If you have forgotten your password, you will be able to reset it. Just input your email in the login screen an hit "reset password". You will then receive an email with a link to reset your password.

After loggin in, you will be able to signout, transfer money, pay bills and check current bills.

Inside the transfer activity, you will be able to type an email. This email can be your own email. If you type your own email, you will be able to transfer money to one of your accounts.

Inside the pay activity, you will be able to pay bills. Paying bills means that you will sent money to a company. Every company has a companyNumber. currently there is only 1 company, with companyNumber: 1111.
You can choose to make a payment, an auto payment or a one-time payment. This is done by either turning the switch on / off. 

A newly registeret user will only have a selection of account. Pension will be unlocked if the user is older than 77. To get more accounts or deposit money to the accounts, the bank will have to do that through the use of Firebase (database). 


