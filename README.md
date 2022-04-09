<h1>This project is still in development</h1>

<h2>Purpose</h2>
This project is meant to represent my current abilities in creating RESTful API's using Spring Boot.
<br>
The list of available endpoints is available after running the app, on this url:
<br><br>
<b>http://[yourserveradress]:8080/api/v1/swagger-ui.html</b>
<h2>Functionality</h2>
This API allows its users to create "courses", (aka groups), in which short assignments can be shared by the course's "teacher" (aka admin).
<br><br>
Each assignment consists of a title, a description and optionally a list of urls that the "students" (aka users) should take a look at.
<br>
The urls functionality could in the future be expanded to support file upload as well.
<br><br>
Every course has at least one teacher, but more can be added from the list of attending students.
<br><br>
The students can join courses by using a 6 character invite code, that the teacher needs to send them.
<br>
Only the teacher can create new assignments, but every student in the course can read them.
<br><br>
Every user has the ability to create his own course.
