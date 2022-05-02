<h1>Classroom API</h1>
A simple assignment management API, inspired by Google Classroom

<h2>Disclaimer</h2>
This project is meant to represent my current skills at creating RESTful APIs using Spring Boot. <br>
It is still in development, so there is probably a lot of bugs and some features may not work as intended.<br>
I'll try to bring this application to the best possible state before eventually moving on to another project.

<h2>Features</h2>
This application allows its users to create and join "courses" - groups, in which the admin can share content for the other users to see.

Every user can create their own course, and invite to join as many users as they like, by sending them a 6 character invite code, which is unique to every course.

By default, the only admin of a course is it's creator, but they can make any other attending user an admin as well.

<h2>Running the app</h2>
For now, there isn't a straightforward way to run the app, other than building it from source.<br>
I'll provide a Docker image in the future.

[You can try the app out by visiting this link, though](https://eukon-classroom.herokuapp.com/api/v1/swagger-ui.html)
