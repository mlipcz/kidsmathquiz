# kidsmathquiz
Web application for simple timed quizzes for concurrent users.

Design

Users open a web page and can connect by giving their username and pressing a button.
As soon as at least 2 users have connected, a quiz can be started (by any user? by the first user?).

The screen should like this:

| 3 * 4 = ? |
| --------- |
| (sandclock) |
| 8 ¦ 10 ¦ 12 ¦ 14 ¦ 16 |
| Answers |
| ####-------- |

Question can be
* Randomly generated as sums or products within a certain range, i.e. up to 30
* Custom defined, e.g. what's the capital city of Switzerland?

An answer has to be given within 5 seconds (settable). A timeout is a bad answer.
Option: only the fastest user gets a point (or an extra point).
The quiz is over after 20 questions (settable).

Technologies:
* Java 8
* Spring boot 2.2.0
* Webjars containing following libraries:
  * JQuery
  * Bootstrap
  * SockJS + WebSocket

The main target is to improve skills in developing realtime web applications.

