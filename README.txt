USER GUIDE:

***This project was created by Isaac Palma Medina, Verónica Quirós Delgado, and Emilio Chang Bustamante***
***Instructions given below are for computers running on Windows 10/11***

Languaje: Java
IDE: IntelliJ IDEA
Database: MySQL

Database setup:
1) Open MySQL command line client and sign in with your credentials.
2) Type "source " + root of the database (just make sure to delete the quotation marks from it ("")). 
E.G: source C:\Users\example\Documents\ChatFinal\Database\dataChat.sql.

NOTES: 
	-You might have to change the user or password for your database. To do that, go to the Database file and open the .properties extension file.
	In there, you change the "database_user" and "database_password" according to your database credentials.

	-Make sure MySQL service is running on your computer. To do that, hit Windows keybind, type "Services" and look up for "MySQL" or "MySQL80".
	If it is not running, start it.

Project setup:
1) Open IntelliJ IDEA
2) Go to File/Open and open the project. NOTE: there are two files in the project: chat and Database. Open the chat file.
3) Once you have open the project, go to File/Project Structure/Modules. In the Dependencies tab, click the "+" icon,
select JARs or Directories, and inside the chat file, search for a file called lib. Inside there, select all the content of it and hit "ok".
Then, do the same but this time you have to add the Database file. Once you locate it, select it and hit ok, and the project should run properly.



FUNCTIONALITY:
This project simulates an instant messaging application.

	-USER REGISTRATION: in order to register, you need to provide an ID and a password. Then hit the Register button and provide a name.
	After you finish your registration, it will automatically log you in. 

	-USER LOGIN: once you have an account, you just need to provide your ID and password.

	-ADD CONTACTS: in order to add contacts, you need to provide the ID of the user and hit Contact. 
	If the ID exists in the database, it will add that user to your contacts.
	
	-CHAT WITH OTHER USERS: before sending messages, you need to double click the contact you want to talk to.

	-CHATS CONTENT STORAGING: to store your chats, we use XML files. Every time a user logs out, this file is created or updated (it is called 
	as the user's ID), and when this user logs in again, the program retrieves the information and puts it in the respective chats. 
