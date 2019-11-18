# Facility Management System (FMS) IIITD
In this project me and my partner [Rajshree Khare](https://github.com/rajshreekhare) designed and implemented a [Facility Management System](https://www.iiitd.ac.in/facilities/fms) (FMS) for IIITD, an improved version of our college’s current FMS. Our task was to create a high quality software by implementing the advanced OOP concepts.


# Table of contents

1. [Project Details](#project-details)
2. [FMS Functions](#fms-functions)
3. [Control Flow](#control-flow)
4. [Database](#database)
	1. [Student](#student)
	2. [Employee](#employee)
	3. [Complaint](#complaint)
5. [Complaint Format](#complaint-format)
	1. [Student Registration](#student-registration) 
	2. [Employee Registration](#employee-registration) 
	3. [Complaint Registration](#complaint-registration) 


<!-- Project Details -->
## Project Details
This is an automated facility management system where task will be automatically assigned to the workers. This portal provides various services to the resident of IIITD like Electrical, Housekeeping, Horticulture, and work done by carpenter and mason, and others. Main functionality of this portal is to provide a platform for booking complaint for repair, performing maintenance or upgradation activities, keeping track record of each employee.

User using the system are Students, FMS Employee and FMS Administrator. There are employee for each type of service like electrician to do electrical work, sweepers for housekeeping, gardeners, carpenter and mason. This will be a text based system. All complaints and facility management will be done through command.

We made a Facebook based system. For this we have created a FMS Facebook page. On that page users can communicate using Facebook post where they will type the command in specific format and further communication (like request for feedback, etc.) regarding that complaint/information will be posted in the reply of that post/message.

<!-- FMS functions -->
## FMS Functions

 - **Registration**: Each employee and student have to register themselves on the system. Administrator of the Facebook page will be the administrator of this system. All details like userid, name, type (employee/student), type of profession (if he is an employee), and all other relevant details are taken at the time of registration.
 - **Complaint/request for Repair**: Student can complaint about a specific service provided or request for repair. User will mention location, type of service needed, and all other relevant details.
 - **Assigning Task**: For complaint and request for repair, system will automatically assign an employee of specific profession as requested by the user to that complaint. After completion of the task, employee will tell the system about status of the job done and student will be asked for confirmation and feedback. The task has to be completed in 24 hours deadline. Count of task completed and task not completed in time/ bad feedback for each employee will be maintained. If number of task completed is 10 less than the other then that employee is reported to the admins.


<!-- Control flow -->
## Control Flow

 - First the employees and the students get themselves registered.
 - A Successful registration is notified by an “Ok.” reply on the post else an error message is posted as reply.
 - Only a registered student can post the complaint on the page.
 - Once the complaint is posted, then the system assigns the task to the employee based on the availability.
- The assigned employee is tagged in the comment as reply, hence he/she gets notified about the task.
-   When the task is completed, the the employee posts “Done.” as a reply on the same post and the task completion time is recorded.
-   Now the system posts another comment asking for the students feedback for the service, which is a rating from 1 to 5, with 1 as the highest rating and 5 as the least.



<!-- Database details -->
## Database
We used to simple [SQL](https://en.wikipedia.org/wiki/SQL) database for our project. We named our primary database **FMS**, which consits of total three tables. Following are the details of each table separately.

### Student
This table is for storing the basic information for the students. We stored the total 4 fields of each students - **Roll No, Name, Room No** and **Facebook Unique ID** of the student.


| Field | Type | Null Value| Key Type | Default Value|
|--|--|--|--|--|
|rollno|varchar(10)|NO|Primary|NULL
|name|varchar(100)|NO|-|NULL
|roomno|varchar(10)|NO|-|NULL
|sfbid|varchar(30)|YES|Unique|NULL

### Employee
This table is for storing the basic information for the FMS employees. We stored the total 4 fields of each students - **Facebook Unique ID, Name, Department** and **No of tasks completed by** of the employee.


| Field | Type | Null Value| Key Type | Default Value|
|--|--|--|--|--|
|efbid|varchar(30)|YES|Primary|NULL
|name|varchar(100)|NO|-|NULL
|dept|varchar(20)|NO|-|NULL
|tasks|int(11)|NO|-|NULL

### Complaint
This table is for storing all the complaint/request details done by the students. Following is the table format and description of each field.


| Field | Type | Null Value | Key Type | Default Value | Description
|--|--|--|--|--|--|
| postid | varchar(100) | NO | Primary | NULL | Facebook generated unique ID of the post |
| sfbid | varchar(30) | NO | - | NULL| Facebook generated unique ID for the student |
| roomno | varchar(10) | NO | - | NULL| Student Room No |
| dept | varchar(20) | NO | - | NULL| Employee's department |
| descr | varchar(500) | NO | - | NULL| Complaint description posted by the student |
| efbid | varchar(30) | NO | Primary | NULL| Facebook generated unique ID for the employee |
| stime | datetime | NO | - | NULL| Task start time |
| etime | datetime | YES | - | NULL| Task end time |
| iscompleted | int(11) | NO | - | 0 | A boolean value to indicate weather the task is completed(1) or not(0) |
| rating | varchar(1) | YES | - | NULL | Rating given by the student on a scale [1-5] |
| israted| int(11) | YES | - | 0 | A boolean column to indicate whether user has provided rating(1) or not(0) |


<!-- Complaint format -->
## Complaint Format
We followed a strict format for all the tasks. Based on the mentioned format we extract the data from the post posted by students or employees on the facebook page using Graph API. 

### Student Registration
 - **Syntax**: `register student rollno roomno_hosteltype`
 - **Example**: `REG STU MT17007 C104_B`

### Employee Registration
 - **Syntax**: `register employee department`
 - **Example**: `REG EMP ELEC`
 
| Departpent Keyword | Departpent Name |
|--|--|
| ELEC | Electrician |
| HOUSE | Housekeeping |
| HORTI | Horticulture |
| CARP | Carpenter |
| MASO | Mason |

### Complaint Registration
 - **Syntax**: `complaint roomno_hosteltype department compaint_description`
 - **Example**: `COMP C404G ELEC Please fix my AC!`
 - 
