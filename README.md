# QueueApp
Android, Java, Firebase, Authentication


This project is to manage a digital queue.

Covered topics are:

1.Login and logout
- Create a user by email
- Login by email authentication
- Reset password account for email authenticators
- Sign out account

2.Create or find a queue
- Everyone can create a queue but with a unique name
- Users first need to search for this queue by its name or status code
- Join the queue and wait for your number, users cannot join twice in a queue
- All queues are saved in user history so if user has join once it’s not needed to research again for it
- Users can join in multiple queues at the same time


3.Manage your queue
- Queue admin is the only who can delete users in queue
- Queue admin can only delete from Queue first user in queue
- Queue admin can delete also all the users in the queue with one command if it is no more time and if there are still some users in it

Features:
- RecyclerView, Adapter, Tab Layout, Custom Appbar
- RecycleView.ItemTouchHelper(Swipe item to delete)
- Authentication with email or phone number
- Getting Device Token
- Login and Logout Activities
- Delete Account
- Retrieve new password
- Firebase Authentication, RealTime Database, 
- Update user profile
- Offline data 
- Searching by name or code
- Ads implementation
- Intent
- Fragment
- Model
