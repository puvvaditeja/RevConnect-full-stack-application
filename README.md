
██████╗ ███████╗██╗   ██╗ ██████╗ ██████╗ ███╗   ██╗███╗   ██╗███████╗ ██████╗████████╗
██╔══██╗██╔════╝██║   ██║██╔════╝██╔═══██╗████╗  ██║████╗  ██║██╔════╝██╔════╝╚══██╔══╝
██████╔╝█████╗  ██║   ██║██║     ██║   ██║██╔██╗ ██║██╔██╗ ██║█████╗  ██║        ██║   
██╔══██╗██╔══╝  ╚██╗ ██╔╝██║     ██║   ██║██║╚██╗██║██║╚██╗██║██╔══╝  ██║        ██║   
██║  ██║███████╗ ╚████╔╝ ╚██████╗╚██████╔╝██║ ╚████║██║ ╚████║███████╗╚██████╗   ██║   
╚═╝  ╚═╝╚══════╝  ╚═══╝   ╚═════╝ ╚═════╝ ╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝ ╚═════╝   ╚═╝   




> **RevConnect** is a full-stack social media platform built with Spring Boot — designed for three distinct account types: 
personal users, content creators, and businesses. One platform. 
Three identities. Infinite possibilities.


## ◈ Account Types

RevConnect supports three distinct account types, each with a tailored feature set:

| Account Type | Description | Exclusive Capabilities |
|:---:|:---|:---|
| 🙍 **PERSONAL** | Everyday users | Posts, follows, connections, privacy toggle |
| 🎨 **CREATOR** | Influencers & content creators | CTA buttons on posts, social link, creator profile, follower demographics |
| 🏢 **BUSINESS** | Brands & local businesses | Product tagging on posts, business profile (address, hours, contact), product/service catalogue |

---

## ◈ All Features

### 1 🔐 Authentication & User Management

| Feature | Details |
|---------|---------|
| **User Registration** | Register with username, email, password, account type, security question & answer |
| **Login with JWT** | Authenticate via email + password → returns a signed JWT token |
| **BCrypt Password Hashing** | All passwords are encrypted using BCrypt before storage |
| **Role-Based Access Control** | `ROLE_USER` for regular users, `ROLE_ADMIN` for admin operations |
| **Account Type Registration** | Choose PERSONAL, CREATOR, or BUSINESS at signup; irrelevant fields auto-cleared per type |
| **Update User** | Update username, email, account type, contact info, privacy setting |
| **Delete User** | Hard-delete a user account from the system |
| **Get All Users** | Admin endpoint to list all registered users |
| **Get User By ID** | Fetch a specific user's data by their ID |
| **Password Reset via Security Question** | Forgot password flow: enter email → retrieve security question → answer → set new password |
| **5 Security Question Options** | First school name, mother's maiden name, favourite food, birth city, childhood nickname |
| **Private/Public Account Toggle** | Switch account visibility between private and public at any time |
| **Duplicate Email Prevention** | Registration blocked if email is already registered |
| **Self-Notification Prevention** | System never sends a user a notification about their own actions |

---

### 2 📝 Posts & Content

| Feature | Details |
|---------|---------|
| **Create Post** | Any authenticated user can create a post with content, hashtag, and image URL |
| **Edit Post** | Post owner can update post content; ownership is strictly verified before edit |
| **Delete Post** | Admin-only deletion enforced with `ROLE_ADMIN` guard |
| **Get Post by ID** | Fetch a single post's full details |
| **Get All Posts** | Retrieve every post on the platform |
| **Get Posts by User** | View a specific user's posts (respects private account visibility rules) |
| **Image Support** | Posts include an `imageUrl` field for attaching media |
| **Hashtag Support** | Posts include a `hashtag` field; auto-prefixed with `#` if missing |
| **Hashtag Search** | Search all posts by hashtag (case-insensitive) |
| **Keyword Search** | Full-text search across post content (case-insensitive) |
| **Post Scheduling** | Set a `scheduledAt` datetime; scheduled posts are hidden from feed until their time arrives |
| **Pin / Unpin Post** | Users can pin their own posts to the top of their profile |
| **View Count Tracking** | Every post view increments and saves the `viewCount` field |
| **Post Sharing (Repost)** | Share another user's post — creates a `Share` record and a linked post referencing `originalPost` |
| **Promotional Posts (Business only)** | Business accounts can attach `productName` and `productLink` to posts |
| **CTA Button (Creator only)** | Creators can add a Call-To-Action button (`ctaLabel` + `ctaLink`) to posts (e.g. "Watch Now") |
| **Personal Post Restriction** | Personal accounts are blocked from creating promotional or CTA posts |
| **Creator Product Restriction** | Creator accounts cannot tag products — only Business accounts can |
| **Duplicate Share Prevention** | A user cannot share the same post more than once |
| **Timestamps Auto-Managed** | `@PrePersist` and `@PreUpdate` hooks auto-set `createdAt` and `updatedAt` |
| **Paginated Posts** | Posts support pagination via Spring Data `Pageable` |
| **Sorted Posts** | Retrieve posts sorted by creation date descending |

---

### 3 📰 Feed System

| Feature | Details |
|---------|---------|
| **Personalised Feed** | Feed aggregates posts from all accounts the user follows + their own posts |
| **Paginated Feed** | Feed returns paginated results ready for infinite-scroll |
| **Scheduled Post Filtering** | Future-scheduled posts are automatically filtered out of the feed |
| **Feed Filter — Following** | Filter feed to show only posts from followed accounts |
| **Feed Filter — Connections** | Filter feed to show only posts from connected users |
| **Feed Filter — Creators** | Filter feed to show only posts from Creator-type accounts |
| **Trending Posts** | Endpoint to retrieve trending posts ranked by engagement |
| **Trending Hashtags** | Retrieve the most-used hashtags platform-wide with usage counts |

---

### 4 👥 Follow System

| Feature | Details |
|---------|---------|
| **Follow User** | Follow any public account |
| **Unfollow User** | Unfollow any account you currently follow |
| **Self-Follow Prevention** | Users cannot follow themselves |
| **Duplicate Follow Prevention** | System rejects a follow request if already following |
| **Get Followers List** | Retrieve all users following a specific account |
| **Get Following List** | Retrieve all accounts a user is following |
| **Followers Count** | Get the total follower count for any user |
| **Following Count** | Get the total number of accounts a user follows |
| **Remove Follower** | Account owners can remove specific followers from their list |
| **Follower Demographics** | Breakdown of follower types (PERSONAL / CREATOR / BUSINESS) for any user |
| **Follow Notification** | Target user is notified when someone new follows them |
| **Private Account Access Control** | Posts of private accounts are only visible to approved followers |

---

### 5 🤝 Connection System

| Feature | Details |
|---------|---------|
| **Send Connection Request** | Send a bidirectional connection request (LinkedIn-style), status starts as `PENDING` |
| **Accept Connection Request** | Receiver can accept a pending request → status becomes `ACCEPTED` |
| **Reject Connection Request** | Receiver can reject a pending request → status becomes `REJECTED` |
| **Remove Connection** | Either party can remove an accepted connection |
| **Get Accepted Connections** | List all accepted connections for a user |
| **Get Pending Requests** | View all incoming pending connection requests |
| **Get Sent Requests** | View all outgoing pending requests the user has sent |
| **Connection Request Notification** | Receiver is notified when a request is sent |
| **Connection Accepted Notification** | Sender is notified when their request is accepted |

---

### 6 🚫 Block System

| Feature | Details |
|---------|---------|
| **Block User** | Block any other user to restrict their interactions |
| **Unblock User** | Remove a block at any time |
| **Self-Block Prevention** | Users cannot block themselves |
| **Duplicate Block Prevention** | System rejects a block if the user is already blocked |
| **Block Status Check** | Internal `isBlocked(blockerId, blockedId)` used across the platform for content gating |

---

### 7 ❤️ Likes

| Feature | Details |
|---------|---------|
| **Like a Post** | Any authenticated user can like any post once |
| **Unlike a Post** | Remove a previously placed like |
| **Live Like Count** | Returns the current total like count for any post after every action |
| **Duplicate Like Prevention** | Error thrown if user tries to like an already-liked post |
| **Get All Likes** | Admin/debug endpoint to list all likes across the platform |
| **Like Notification** | Post owner is notified when someone likes their post |

---

### 8 💬 Comments & Replies

| Feature | Details |
|---------|---------|
| **Add Comment** | Add a comment to any post |
| **Delete Comment** | Comment owner can delete their own comment; ownership strictly enforced |
| **Get Comments by Post** | Retrieve all comments for a specific post |
| **Reply to Comment** | Reply directly to any existing comment (nested/threaded comment structure) |
| **Empty Reply Validation** | Reply content cannot be null or blank — validation enforced at service layer |
| **Comment Notification** | Post owner is notified when someone comments on their post |
| **Comment Count** | Used in post analytics to count total comments per post |

---

### 9 🔁 Share System

| Feature | Details |
|---------|---------|
| **Share a Post** | Share any post — creates a `Share` record and links the shared content to the original |
| **Duplicate Share Prevention** | A user cannot share the same post more than once |
| **Share Count** | Used in analytics, engagement metrics, and profile stats |
| **Share Notification** | Original post owner is notified when their post is shared |
| **Self-Share Notification Skipped** | If a user shares their own post, no redundant notification is sent |

---

### 10 🔖 Saved Posts

| Feature | Details |
|---------|---------|
| **Save a Post** | Bookmark any post for later access |
| **Unsave a Post** | Remove a post from your saved list |
| **Get Saved Posts** | Retrieve all posts a user has saved, returned as clean `PostResponse` DTOs |
| **Duplicate Save Prevention** | Cannot bookmark the same post more than once |

---

### 11 🔔 Notifications

| Feature | Details |
|---------|---------|
| **Notification Types** | `LIKE`, `COMMENT`, `FOLLOW`, `SHARE`, `CONNECTION_REQUEST`, `CONNECTION_ACCEPTED` |
| **Get All Notifications** | Retrieve all notifications for a user, ordered by newest first |
| **Get Unread Notifications** | Retrieve only unread notifications |
| **Mark Notification as Read** | Mark a single notification as read |
| **Unread Count** | Get total unread notification count (for badge/indicator display) |
| **Auto-Triggered Notifications** | Notifications are created automatically on: like, comment, follow, share, connection request, connection accepted |
| **Self-Notification Prevention** | Notification is skipped if sender and receiver are the same user |
| **Notification Response DTO** | Returns: notification ID, sender ID & name, message, type, read status, and timestamp |

---

### 12 ⚙️ Notification Preferences

| Feature | Details |
|---------|---------|
| **Get Preferences** | Fetch a user's current preferences (auto-creates default record if none exists) |
| **Update Preferences** | Toggle individual notification categories on or off |
| **Like Notifications Toggle** | Enable/disable notifications for post likes |
| **Comment Notifications Toggle** | Enable/disable notifications for post comments |
| **Follow Notifications Toggle** | Enable/disable notifications for new followers |
| **Share Notifications Toggle** | Enable/disable notifications for post shares |
| **Auto-Default Creation** | If no preferences record exists for a user, defaults are created automatically on first fetch |

---

### 13 👤 Profile Management

| Feature | Details |
|---------|---------|
| **Get Profile** | View a user's full profile including live-calculated stats |
| **Update Profile** | Update name, bio, profile picture, website link, and location |
| **Profile Picture** | Store and display a profile picture via URL |
| **Bio** | Short user biography field on every profile |
| **Website Link** | External website or portfolio link |
| **Location** | User's location field |
| **Live Profile Stats** | Total posts, total followers, total following, total shares — all computed dynamically |
| **Partial Update Support** | Only non-null fields are updated; existing data is preserved |
| **Business Profile Fields** | Business accounts: category, contact info, address, business hours, social link, products/services description |
| **Creator Social Link** | Creators can store a social link (YouTube, Instagram, TikTok, etc.) |
| **Update Business Profile** | Dedicated endpoint for updating Business-specific profile fields only |

---

### 14 🔍 User Search & Discovery

| Feature | Details |
|---------|---------|
| **Search by Username** | Case-insensitive partial-match search by username |
| **Search by Email** | Case-insensitive partial-match search by email |
| **Combined Keyword Search** | Single query searches both username and email fields simultaneously |
| **No Result Handling** | Returns `ResourceNotFoundException` with a clear message when no users are found |
| **Lightweight Search Response** | Returns `UserSearchResponse` DTO — not the full user entity |

---

### 15 🛍️ Business Products

| Feature | Details |
|---------|---------|
| **Product Catalogue** | Business accounts can maintain a dedicated product/service list |
| **Tag Product on Post** | Business posts can reference a linked `Product` entity |
| **Quick Product Tagging** | Lightweight tagging via `productName` + `productLink` without requiring a full Product entity |
| **Products/Services Description** | Free-text field on Business user profile for describing all offerings |

---

### 16 📊 Analytics & Engagement

| Feature | Details |
|---------|---------|
| **Post Analytics** | Per-post breakdown: total likes, total comments, total shares |
| **Post View Count** | Tracks and increments `viewCount` every time a post is opened |
| **Engagement Metrics** | Combined engagement score per post: likes + comments + shares |
| **Profile-Level Stats** | Profile page shows: total posts, followers, following, and shares |
| **Follower Demographics** | Breakdown of a user's followers by account type (PERSONAL / CREATOR / BUSINESS) |
| **Follower Analytics Entity** | Dedicated `FollowerAnalytics` table for tracking follower growth over time |
| **Post Analytics Response DTO** | Clean API response: postId, totalLikes, totalComments, totalShares |
| **Trending Hashtags** | Ranked list of most-used hashtags across the platform with counts |
| **Trending Posts** | Endpoint to surface the most-engaged posts platform-wide |

---

### 17 🛡️ Privacy & Security

| Feature | Details |
|---------|---------|
| **JWT Authentication** | Fully stateless authentication — no server-side sessions |
| **JWT Token with Role Claims** | Token encodes user email + role for all authorisation decisions |
| **Custom JWT Filter** | `JwtAuthenticationFilter` intercepts and validates every request before it reaches controllers |
| **BCrypt Password Encryption** | Industry-standard hashing with salt for all stored passwords |
| **Spring Security Integration** | Full `SecurityFilterChain` configuration with endpoint-level protection |
| **Role-Based Endpoint Guards** | `@PreAuthorize("hasRole('USER')")` and `@PreAuthorize("hasRole('ADMIN')")` annotations |
| **Admin-Only Delete** | Post deletion restricted to `ROLE_ADMIN` only |
| **Private Account Enforcement** | Posts of private accounts blocked for non-followers at the service layer |
| **Resource Ownership Verification** | All edit/delete operations verify the requesting user owns the resource |
| **Unauthorised Action Exception** | `UnauthorizedException` thrown with clear message on forbidden operations |
| **Global Exception Handler** | `GlobalExceptionHandler` catches and formats all exceptions into clean API error responses |
| **Custom Exception Types** | `BadRequestException`, `ResourceNotFoundException`, `UnauthorizedException` |
| **Security Question Recovery** | Password reset without email links — verify identity via pre-set security question |
| **Log4j2 Structured Logging** | All application activity logged via Log4j2 with rolling file support in `/logs` |

---

## ◈ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (JJWT 0.11.5) |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| View Layer | Thymeleaf |
| Build Tool | Maven (with Maven Wrapper) |
| Logging | Log4j2 |
| Testing | Spring Boot Test, JUnit 5 |

---

## ◈ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT LAYER                         │
│        Thymeleaf Templates  +  Static CSS               │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                 CONTROLLER LAYER                        │
│   REST Controllers (/api/**)                            │
│   MVC Controllers  (Thymeleaf views)                    │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                  SECURITY LAYER                         │
│   JwtAuthenticationFilter → SecurityContextHolder       │
│   CustomUserDetailsService → UserDetailsService         │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                  SERVICE LAYER                          │
│  AuthService | UserService | PostService | FollowService│
│  CommentService | LikeService | ShareService            │
│  NotificationService | ConnectionService | BlockService │
│  SavedPostService | ProfileService | ...                │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                REPOSITORY LAYER                         │
│           Spring Data JPA Repositories                  │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│                  DATABASE LAYER                         │
│               MySQL  (revconnect2)                      │
└─────────────────────────────────────────────────────────┘
```

---

## ◈ Project Structure

```
revconnect/
├── src/
│   ├── main/
│   │   ├── java/com/example/revconnect/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/              ← REST API Controllers
│   │   │   │   ├── AuthController
│   │   │   │   ├── PostController
│   │   │   │   ├── FollowController
│   │   │   │   ├── CommentController
│   │   │   │   ├── LikeController
│   │   │   │   ├── ShareController
│   │   │   │   ├── ConnectionController
│   │   │   │   ├── BlockController
│   │   │   │   ├── NotificationController
│   │   │   │   ├── NotificationPreferenceController
│   │   │   │   ├── ProfileController
│   │   │   │   ├── SavedPostController
│   │   │   │   ├── ProductController
│   │   │   │   └── UserController
│   │   │   ├── mvccontrollers/          ← Thymeleaf View Controllers
│   │   │   │   ├── AuthMvcController
│   │   │   │   ├── FeedMvcController
│   │   │   │   ├── FollowMvcController
│   │   │   │   ├── NotificationMvcController
│   │   │   │   ├── PostMvcController
│   │   │   │   ├── ProfileMvcController
│   │   │   │   └── UserSearchMvcController
│   │   │   ├── service/                 ← Business Logic
│   │   │   ├── repository/              ← JPA Data Access
│   │   │   ├── entity/                  ← JPA Entity Models
│   │   │   │   ├── User, Post, Comment, Like, Share
│   │   │   │   ├── Follow, Connection, Block
│   │   │   │   ├── Notification, NotificationPreference
│   │   │   │   ├── Profile, SavedPost, Product
│   │   │   │   ├── PostAnalytics, FollowerAnalytics
│   │   │   │   └── Enums: AccountType, Role, NotificationType, SecurityQuestion
│   │   │   ├── dto/                     ← Data Transfer Objects
│   │   │   ├── security/                ← JWT + Spring Security
│   │   │   │   ├── JwtUtil
│   │   │   │   ├── JwtAuthenticationFilter
│   │   │   │   └── CustomUserDetailsService
│   │   │   └── exception/               ← Exception Handling
│   │   │       ├── GlobalExceptionHandler
│   │   │       ├── BadRequestException
│   │   │       ├── ResourceNotFoundException
│   │   │       └── UnauthorizedException
│   │   └── resources/
│   │       ├── templates/               ← Thymeleaf HTML Pages
│   │       ├── static/css/              ← Stylesheets
│   │       ├── application.properties
│   │       └── log4j2.xml
│   └── test/                            ← Unit & Integration Tests
├── pom.xml
└── mvnw
```

---

## ◈ API Reference

### 🔑 Auth & Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/login` | Public | Login → returns JWT |
| `POST` | `/api/users/register` | Public | Register new user |
| `POST` | `/api/users/reset-password` | Public | Reset password via security question |
| `GET` | `/api/users/security-question?email=` | Public | Get security question for email |
| `GET` | `/api/users` | 🔒 | Get all users |
| `GET` | `/api/users/{id}` | 🔒 | Get user by ID |
| `PUT` | `/api/users/{id}` | 🔒 | Update user |
| `DELETE` | `/api/users/{id}` | 🔒 ADMIN | Delete user |
| `GET` | `/api/users/search?keyword=` | 🔒 | Search users |
| `PUT` | `/api/users/{id}/privacy` | 🔒 | Update privacy setting |
| `GET` | `/api/users/{id}/follower-demographics` | 🔒 | Get follower demographics |
| `PUT` | `/api/users/{id}/business-profile` | 🔒 | Update business profile fields |

### 📝 Posts
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/posts/user/{userId}` | 🔒 USER | Create post |
| `GET` | `/api/posts` | Public | Get all posts |
| `GET` | `/api/posts/{postId}` | Public | Get post by ID |
| `GET` | `/api/posts/view/{postId}` | Public | View post (increments view count) |
| `GET` | `/api/posts/user/{viewerId}/{targetUserId}` | 🔒 | Get posts by user |
| `PUT` | `/api/posts/user/{userId}/post/{postId}` | 🔒 USER | Edit post |
| `DELETE` | `/api/posts/user/{userId}/post/{postId}` | 🔒 ADMIN | Delete post |
| `GET` | `/api/posts/feed/{userId}` | 🔒 | Get personalised feed |
| `GET` | `/api/posts/feed/{userId}/filter?type=` | 🔒 | Filtered feed (following / connections / creator) |
| `GET` | `/api/posts/trending` | Public | Get trending posts |
| `GET` | `/api/posts/trending-hashtags` | Public | Get trending hashtags |
| `GET` | `/api/posts/search?keyword=` | Public | Search posts by keyword |
| `GET` | `/api/posts/hashtag/{tag}` | Public | Search posts by hashtag |
| `PUT` | `/api/posts/{postId}/pin/{userId}` | 🔒 | Pin a post |
| `PUT` | `/api/posts/{postId}/unpin/{userId}` | 🔒 | Unpin a post |
| `GET` | `/api/posts/{postId}/analytics` | 🔒 | Get post analytics |
| `GET` | `/api/posts/{postId}/engagement` | 🔒 | Get engagement metrics |

### 👥 Follow
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/follow/{followerId}/follow/{followingId}` | 🔒 | Follow user |
| `DELETE` | `/api/follow/{followerId}/unfollow/{followingId}` | 🔒 | Unfollow user |
| `GET` | `/api/follow/{userId}/followers` | 🔒 | Get followers list |
| `GET` | `/api/follow/{userId}/following` | 🔒 | Get following list |
| `GET` | `/api/follow/{userId}/followers/count` | 🔒 | Followers count |
| `GET` | `/api/follow/{userId}/following/count` | 🔒 | Following count |
| `DELETE` | `/api/follow/{targetUserId}/remove-follower/{followerId}` | 🔒 | Remove a follower |
| `GET` | `/api/follow/{userId}/demographics` | 🔒 | Follower demographics |

### 🤝 Connections
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/connections/request` | 🔒 | Send connection request |
| `PUT` | `/api/connections/{connectionId}/accept` | 🔒 | Accept request |
| `PUT` | `/api/connections/{connectionId}/reject` | 🔒 | Reject request |
| `DELETE` | `/api/connections/{userId}/remove/{connectionUserId}` | 🔒 | Remove connection |
| `GET` | `/api/connections/{userId}` | 🔒 | Get accepted connections |
| `GET` | `/api/connections/{userId}/pending` | 🔒 | Get pending requests |
| `GET` | `/api/connections/{userId}/sent` | 🔒 | Get sent requests |

### 🚫 Block
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/block/{blockerId}/block/{blockedId}` | 🔒 | Block user |
| `DELETE` | `/api/block/{blockerId}/unblock/{blockedId}` | 🔒 | Unblock user |

### ❤️ Likes
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/likes/post/{postId}/user/{userId}` | 🔒 | Like post |
| `DELETE` | `/api/likes/post/{postId}/user/{userId}` | 🔒 | Unlike post |
| `GET` | `/api/likes/post/{postId}/count` | Public | Get like count |

### 💬 Comments
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/comments` | 🔒 | Add comment |
| `GET` | `/api/comments/post/{postId}` | Public | Get post comments |
| `DELETE` | `/api/comments/{commentId}/user/{userId}` | 🔒 | Delete comment |
| `POST` | `/api/comments/{commentId}/reply/{userId}` | 🔒 | Reply to comment |

### 🔁 Shares
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/shares` | 🔒 | Share a post |

### 🔖 Saved Posts
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/saved/user/{userId}/post/{postId}` | 🔒 | Save a post |
| `DELETE` | `/api/saved/user/{userId}/post/{postId}` | 🔒 | Unsave a post |
| `GET` | `/api/saved/user/{userId}` | 🔒 | Get saved posts |

### 🔔 Notifications
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/notifications/{userId}` | 🔒 | Get all notifications |
| `GET` | `/api/notifications/{userId}/unread` | 🔒 | Get unread notifications |
| `PUT` | `/api/notifications/{id}/read` | 🔒 | Mark as read |
| `GET` | `/api/notifications/{userId}/count` | 🔒 | Get unread count |

### ⚙️ Notification Preferences
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/notification-preferences/{userId}` | 🔒 | Get preferences |
| `PUT` | `/api/notification-preferences/{userId}` | 🔒 | Update preferences |

### 👤 Profile
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/profile/{userId}` | 🔒 | Get profile |
| `PUT` | `/api/profile/{userId}` | 🔒 | Update profile |

---

## ◈ Getting Started

### Prerequisites

- ☕ **Java 17** or higher
- 🐬 **MySQL 8.x**
- 📦 **Maven 3.8+** (or use the bundled `./mvnw`)

### 1 — Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/revconnect.git
cd revconnect
```

### 2 — Set Up the Database

```sql
CREATE DATABASE revconnect2;
```

### 3 — Configure application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/revconnect2
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8282
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

> ⚠️ **Never commit real credentials.** Use environment variables or a secrets manager in production.

### 4 — Build & Run

```bash
# Using the Maven Wrapper (no Maven installation needed)
./mvnw spring-boot:run

# Or with Maven installed globally
mvn spring-boot:run
```

### 5 — Access the App

| Interface | URL |
|-----------|-----|
| 🌐 Web App | `http://localhost:8282` |
| 🔐 Login | `http://localhost:8282/login` |
| 📋 Register | `http://localhost:8282/register` |
| 🔌 REST API | `http://localhost:8282/api` |

---

## ◈ Thymeleaf Pages

| Template | Description |
|----------|-------------|
| `login.html` | Login page |
| `register.html` | Account registration |
| `feed.html` | Main social feed |
| `profile.html` | User profile view |
| `edit-profile.html` | Edit profile details |
| `create-post.html` | Create a new post |
| `edit-post.html` | Edit an existing post |
| `post-detail.html` | Single post detail view |
| `notifications.html` | Notification centre |
| `user-search.html` | Search for users |
| `saved-posts.html` | Bookmarked posts |
| `followers.html` | Followers list |
| `following.html` | Following list |
| `forgot-password.html` | Password recovery via security question |

---

## ◈ Running Tests

```bash
# Run all tests
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AuthControllerTest
```

### Test Coverage

| Layer | Test Classes |
|-------|-------------|
| Controllers | `AuthControllerTest`, `PostControllerTest`, `FollowControllerTest`, `UserControllerTest` |
| Services | `AuthServiceTest`, `PostServiceTest`, `FollowServiceTest`, `UserServiceTest` |
| Integration | `RevconnectApplicationTests` |

---

<div align="center">

---

**Built with ☕ Java, 💚 Spring Boot, and a drive to connect people.**

*RevConnect — Rethink the way you connect.*

</div>
