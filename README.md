<h1>Classroom API</h1>
A simple assignment management API, inspired by Google Classroom

<h2>Disclaimer</h2>
This project is meant to represent my current skills at creating RESTful APIs using Spring Boot. <br>
It is still in development, so there is probably a lot of bugs and some features may not work as intended.<br>
I'll try to bring this application to the best possible state before eventually moving on to another project.

<h2>Features</h2>
This application allows its users to create and join "courses" - groups, in which the admin can share content for the other users to see.

Every user can create their own course, and invite to join as many users as they like, by sending them a 6 character invite code, which is unique to every course.

By default, the only admin of a course is its creator, but they can make any other attending user an admin as well.

[OpenAPI documentation of the project is available here](https://eukon-classroom.herokuapp.com/api/v1/swagger-ui.html)

<h2>Running the app</h2>
You can run the app on your own server with Docker Compose:

- Download the `docker-compose.yml` file from this repo and save it in a new directory
- Change the `JWT.SECRET` variable in the file to a random string, I also <b>STRONGLY</b> recommend changing the database credentials.
- Run the following command in the directory containing the file:<br>
`docker-compose up`

You can also run the app with a regular Docker image and providing your own database.
Here's a command that you'll have to run in order to start the app up on your machine:
```
docker run \
-e SPRING.DATASOURCE.URL=jdbc:postgresql://yourdatabaseurl \
-e SPRING.DATASOURCE.USERNAME=yourdatabaseusername \
-e SPRING.DATASOURCE.PASSWORD=yourdatabasepassword \
-e JWT.SECRET=randomstringhere \
eukon/classroom
```
Please note that the command above is intended for Linux hosts. <br>
If you want to use Windows, replace the `` \ `` with `` ` ``

<h2>Contributing</h2>
If you have an idea for a new feature or a bugfix, feel free to open an issue or a pull request!
