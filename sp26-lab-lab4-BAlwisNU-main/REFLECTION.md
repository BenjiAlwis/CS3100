# Lab 4 Reflection

Complete this reflection as part of your lab submission. It is **essential** that you complete this file in order to receive credit for the lab.

**Partner's Name:** Aobo

## Part 1 Analysis

### 1. Coupling in the Mud

What type of coupling did you identify in the "big ball of mud" implementation? (Use vocabulary from Lecture 7: control, stamp, data, common, or content coupling)

> *Your answer here*
The “big ball of mud” design shows control coupling. The NotificationManager.notifyUser(...) method takes a NotificationType parameter, and the internal behavior is selected via if/else conditionals based on that control flag. This means adding a new notification type requires modifying the notification manager’s logic, and callers must understand the meaning of the type value to get the expected behavior.

### 2. Your Ratings Table

Enter your ratings table from Exercise 3.1 and explain your reasoning for one row (e.g., why you rated the approaches the way you did for "cross-cutting concerns").

| Category | Mud | Strategy (Classes) | Functional (Lambdas) |
|----------|-----|-------------------|---------------------|
| New channels | 2 | 4 | 4 |
| New filtering logic | 2 | 4 | 5 |
| Cross-cutting concerns | 1 | 3 | 5 |

**Explanation for one row:**

> *Your answer here*

I rated Mud = 1 because cross-cutting concerns like logging, auditing, rate limiting, or region-based routing would require editing the central conditional method. I rated Strategy = 3 because cross-cutting changes can be implemented via wrappers/decorators around strategies or by creating logging strategies, but this can still require touching multiple classes depending on how broadly the concern applies. I rated Functional = 5 because cross-cutting concerns are straightforward to compose.


### 3. Scenario Analysis

For Scenario D (the GDPR surprise), which implementation did you think handled it best? Could we have anticipated this *category* of change even without knowing the specific requirement?

> *Your answer here*

The Functional (Lambdas) approach handles it best because it makes “all notifications must pass through an extra routing step” easy to express as a wrapper around sendNotification. This avoids rewriting conditional logic spread across multiple branches/classes and supports cross-cutting routing policies cleanly.

Yes, we could have anticipated this category of change even without knowing the specific requirement. Regulatory constraints are a form of cross-cutting concern (like auditing or region-based processing). A design that makes cross-cutting behavior composable reduces the cost of such surprises.

## Synthesis

### 4. Functional vs OO

In your own words, when should you use lambdas vs. class-based strategies? Give a concrete example of each.

> *Your answer here*

Use lambdas when strategies are small and composable, for example: adding a policy gate and audit logging without creating new classes:
shouldNotify = ctx -> !ctx.getUser().isInQuietHours() || ctx.getDevice().isCritical();
sendNotification = (u, m) -> { audit.log(u, m); pushService.send(u, m); };
Use class-based strategies when behaviors are substantial, long-lived, or benefit from strong naming and structured testing, for example: an ImmediateNotificationStrategy or DailyDigestNotificationStrategy that clearly depends on a specific service.


### 5. The Production Decision

Which approach (or combination) would you choose for a production notification system? Why?

> *Your answer here*

For a production system with 10+ channels and complex preferences, I would choose a hybrid approach:
I will use Strategy classes to represent major notification modes or channel implementations (clear names, discoverability, dependency management, and unit testing) and then I will use functional composition for cross-cutting concerns and fast-evolving filtering/routing policies (logging/auditing, region routing, feature flags, small predicate-style rules).



## Meta

### 6. Personal Takeaway

What's one thing you'll think about differently when designing code after this lab? Be specific.

> *Your answer here*

I will be more concerned about dependencies and coupling that could make the code harder to change later.

### 7. Disagreement and Resolution

Describe a point where you and your partner initially disagreed. What was each position? What convinced one of you to change your mind (or how did you find a middle ground)?

> *Your answer here*
We discussed about control coupling in the mud and we agreed on the main points.

