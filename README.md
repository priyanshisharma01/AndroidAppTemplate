## This is the template of an Android App Made with Java and XML, Which demonstarte how to fullfill the below requirements and Perform REST Api integration. Use this template and your own Api.



# Requirements
You will be assessed on your ability to complete all the below features. You will not be judged
on how beautiful you can make your UI, but please try to use layout margins/padding to space
your views appropriately, and specify text sizes to make things more readable, where
necessary. The documentation for the REST API that should be used for these tasks is included
at the end of this document.
## 1. A login screen
○ Customer Service agents provide a username and password and tap a button to
submit.
○ If the provided credentials are rejected by the server, then an error should be
displayed.
○ If valid credentials are provided, then the app should advance to the next screen,
and use the auth token in the response for all subsequent requests.
○ Your username will be your email address, and your password should be your
email address in reverse. Please make sure to use a real email address that
you own for this exercise.

## 2. A screen that displays all message threads
○ Display a list of message threads, where each customer’s messages should be
treated as a separate private thread.
○ The latest message of each thread should be surfaced in this list. At a minimum,
please display the message body, timestamp, and agent OR user id (whichever
of these is relevant) of the sender.
○ If any of the message threads in the list is tapped upon, then the app should
advance to the next screen.

## 3. A conversation screen to view an individual message thread and respond

○ A sorted list of all messages (both customer and agent) relevant to the thread
should be displayed here.
○ The agent should be able to respond to the customer using an input field.
○ At a minimum, please display the message body, timestamp, and agent OR user
id (whichever of these is relevant) of the sender.
