# Lab 5: Requirements Engineering Reflection

## Partner Information

- **Your Pair Partner:** [Kerem]
- **Your Quad Partners:** [Darrien, Ryan]

---

## Part 1: Individual Interpretation

### Exercise 1.1: Your Requirements

List 5-7 specific requirements you believe the system needs.

**Requirement 1:**
> **Requirement:** The system shall allow a student to submit a help request that includes the course, topic, question text, and preferred help mode (in-person or virtual).
> **Assumption:**  “Request help” implies a structured request with enough context for a TA to prepare.
> **Stakeholder:**  Student — enables them to clearly communicate what help they need.

**Requirement 2:**
> **Requirement:** The system shall display a real-time queue for each office hours session, showing a student’s position in line and estimated wait time.
> **Assumption:** Students should know when they can expect a response” applies to live office hours queues.
> **Stakeholder:** Student — reduces uncertainty and wasted waiting time.

**Requirement 3:**
> **Requirement:** The system shall allow TAs to mark themselves as available or unavailable during scheduled office hours and accept or defer requests.
> **Assumption:** TAs need explicit control over workload rather than automatic assignment.
> **Stakeholder:** TA — helps manage workload and prevent overload.

**Requirement 4:**
> **Requirement:** The system shall support asynchronous questions by allowing students to submit questions outside live office hours and receive a response within a configurable time window.
> **Assumption:** “Leaving questions for later” refers to asynchronous, non-live help.
> **Stakeholder:** Student — allows help even when live office hours are unavailable.

**Requirement 5:**
> **Requirement:** The system shall enforce fair workload distribution by limiting the number of active help requests assigned to a TA at any time.
> **Assumption:** “Manage workload fairly” implies system-level constraints, not just social norms.
> **Stakeholder:** TA / Department — prevents burnout and inequitable task distribution.

**Requirement 6 (optional):**
> **Requirement:** The system shall display all office hours times converted to each user’s local time zone.
> **Assumption:** “Three campuses in different time zones” requires automatic time-zone handling.
> **Stakeholder:** Student and TA — avoids missed or late attendance.

**Requirement 7 (optional):**
> **Requirement:** [A specific, testable requirement]
> **Assumption:** [What you assumed about the brief to arrive at this requirement]
> **Stakeholder:** [Who benefits and why]

### Exercise 1.2: Ambiguity Identification

List 3 or more terms or phrases from the brief that you found unclear or ambiguous.

**Ambiguity 1:**
> **Phrase:** “Students can request help with course material”
> **Why ambiguous:** It’s unclear whether this includes conceptual questions, debugging help, exam prep, or administrative issues
> **My assumption:** Requests are limited to conceptual and assignment-related academic questions.

**Ambiguity 2:**
> **Phrase:** “The system should support both live help and leaving questions for later”
> **Why ambiguous:** “Leaving questions” could mean email, forum posts, or a formal ticketing system.
> **My assumption:** Leaving questions refers to an asynchronous ticket-style system within the same platform.

**Ambiguity 3:**
> **Phrase:** “Students should know when they can expect a response”
> **Why ambiguous:** This could mean a guaranteed deadline, an estimated wait time, or a simple notification.
> **My assumption:** The system provides estimated wait times for live help and response deadlines for asynchronous questions.

---

## Part 2: Pair Specification

### Exercise 2.1: Interpretation Discussion Notes

Key agreements:
- The system shall allow a student to submit a help request that includes the course, topic, question text, and preferred help mode (in-person or virtual).
- The system shall display a real-time queue for each office hours session, showing a student’s position in line and estimated wait time.
- The system shall allow TAs to mark themselves as available or unavailable during scheduled office hours and accept or defer requests.
- The system shall enforce fair workload distribution by limiting the number of active help requests assigned to a TA at any time.
- The system shall display all office hours times converted to each user’s local time zone.



Key disagreements:
- I would alow for students to submit requests outside of office hours

Interesting assumption differences:
- When seeing TAs online, being able to submit work

### Exercise 2.2: Joint Specification (8 Requirements)

1. Seeing whether students can submit requests outside of office hours (physicaly outside)
2. Checking whether students cannot send more than a certain amount of requests that exceed the maximum number of requests 
3. Checking whether students can see whether their request is in the request queue
4. Checking whether TAs can change their status for availability
5. Whether the students can see availibility change for individual TAs
6. Checking whether students can se office hour times for their specific Time Zone
7. Checking whether student can submit a help request that includes the course, topic, question text, and preferred help mode (in-person or virtual).
8. Checking whether TAs can change their office hour availability

**Shared Assumptions:**
- The system shall allow a student to submit a help request that includes the course, topic, question text, and preferred help mode (in-person or virtual).
- The system shall display a real-time queue for each office hours session, showing a student’s position in line and estimated wait time.
- The system shall allow TAs to mark themselves as available or unavailable during scheduled office hours and accept or defer requests.
- The system shall enforce fair workload distribution by limiting the number of active help requests assigned to a TA at any time.
- The system shall display all office hours times converted to each user’s local time zone.

---

## Part 3-4: The Reveal

### Exercise 3.1: Specification Comparison Notes

Observations while listening to other pair:
- Students can create a help request by typing a question
- Students should not ve able to make more than 3 requests
- When waiting for ofice hours, a student should be able to se their queue position and wait time
- TAs and students should have a shared calendar that alows TAs to edit meting start time and duration
- Students should be distributed beetween TAs equaly. Each TA should be responsible for the same amount of students


### Exercise 3.2: Divergence Catalog

| Topic | Pair AB's Interpretation | Pair CD's Interpretation | Impact if Built Wrong |
|-------|-------------------------|-------------------------|----------------------|
| TA workload should be distributed equally | Limited amount of online requests | Each TA should be responsible for the same amount of students | Certain TAs could be overloaded with work, and they may not be able to complete al their work |
| Timezone | All the timezones should be set to the same gloabl time zone| Al calendar times are set to the same local time zone | |
| Online office hours | Don't allow to submit outside of Office hours | Have zoom links to Office hours | Might not give acesability to students to office hours |
| Request Limit | Modifiable limit to no. request per student | Had 3 request limit per students | Not give enough oportunity for students to ask questions |

### Exercise 4.1: Risk Classification

| Divergence | Risk Type | Why This Classification? |
|------------|-----------|-------------------------|
| Time Zone | Volatility | How easy is it to understand the time zone diferences |
| Online Office hours | Understanding | Suiting the needs of student availibility |
| Request Links | Understanding | To give adequest oportunities to ask questions but not to many |

### Exercise 4.2: What Would Have Helped?

| Divergence | Elicitation Technique | Specific Action |
|------------|----------------------|-----------------|
| Time Zone | Interview | Survey and ask multiple people |
| Online Office hours | Prototype | Try out diferent formats or tols for students to engage with TAs |
| Request Links | Interview & Prototype | Try out different limit values to number of requests students can send per assignment, then ask students on whether they had enough oportunity to ask questions |

---

## Part 5: Reflection and Insights

### Exercise 5.1: User Perspective

**Which interpretation would you want as a student?**
I would want a system that is more oriented around accesibility and flexibility

**Did the "engineer" interpretation match what the "user" would want?**
It for the most part but maybe more flexibility

**Requirements neither pair thought of that you'd want as a user:**
I would want a way to see prompts of questions to ask from the TA to help ask the right questions


### Exercise 5.2: Questions for the Client

1. How much opportunity do you want to give to students to ask questions/send requests

2. Do you want students to submit and orient asignments around a specific time zone or how do you want students to see assignments around different time zones

3. What format for online vs in person sessions do you want students to access office hours

---

## Meta Reflection

### 1. Most Surprising Divergence

What difference between the pairs surprised you most? Why didn't you anticipate it?
I didn't anticipate different views around in-person vs online office hours based on experiences.


### 2. Engineer vs. User

Did your "engineer" interpretation match what you'd actually want as a user of this system? What does that tell you about requirements gathering?



### 3. Future Behavior

What will you do differently when reading requirements for your next assignment or project?



