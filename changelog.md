# Change Log

### 0.17.4
- Introduced karma system
  - Karma system was developed so members could give or take from other members to help show appreciation.
  - Admin karma commands added
- Fixed bug with who command


### 0.17.3
- Changed referenceTriggers to send direct message unless -show used
- Updated who command with more information
- Added class for SQLite DB Handling
  - Added method to add privileged users to db automatically.
- Added AutoRemove class to remove privileged roles from user for setting inactive status.
- Centralized command logging and rank check

### 0.17.2
- Changed info and reference triggers to allow to be called in private messages.
- Local Polls
- Found and fixed bug on Hall Monitor Class

### 0.17.1
- Added Janitor class
  - Removes !stream triggers and messages after a set delay
- Fixed a recursion bug on command loader
- Added alternative method for welcoming new users (Users with direct messages turned off)
- Automatic delete vulgar messages after 30 seconds if not resolved.


### 0.17.0
- Added command alias framework
  - Modified command check method

### 0.16.8
- Added HoneyStatus ("Collecting pollen from (USER)")

### 0.16.7
- Added logging to Welcome message for debugging and analytics.
- Added changelog command to provide link to this github page.
- Added automatic status changes
- Added RefList command

### Before 0.16.7
No history.