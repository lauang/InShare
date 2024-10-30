# Mandatory Assignment 3 – INF226 – 2024

Welcome to the third and final mandatory assignment of INF226 (Software Security). In this assignment, you will be improving the security of a program called InShare—a note-sharing web application that has been deliberately crafted to include a number of security flaws. As you discovered in the previous assignment, InShare suffers from vulnerabilities that compromise its security.

Your task is to analyze these vulnerabilities, plan improvements, and ultimately secure the application. You will be working in phases, focusing on design, implementation and review.

From the learning outcomes of the course:

 - "The student masters, theoretically and practically, programming techniques to develop
secure, safe, reliable, and robust systems, and can assess the security of given source code or application."
 - "The student can plan and carry out varied assignments and projects for secure software, can develop critical thinking about secure software, can exchange opinions with other professionals and participate in developing best practices for secure software."


## Group Work

This project is to be carried out by groups of 1–3 students. You may choose to retain your previous group from earlier assignments, or you can form a new group. Make sure that everyone is signed up for the group on MittUiB. **Note:** This assignment includes a bit more programming than the previous assignments, so even if you worked alone on the previous assignments, you may consider forming a group for this one. Even if you prefer to work alone, you are strongly
 encouraged to find another which can review your code.

We encourage collaboration through GitLab for branching, merge requests, and peer review. Each member of the group is expected to contribute to both the analysis and implementation of the security improvements.

# Phases and Iteration

The assignment is divided into three phases: Planning, Implementation and Review. These phases are not disjoint, and you should expect a feedback loop where planning, implementation, testing and review may cycle multiple times.

You will be working on each phase simultaneously, addressing different areas of security (e.g., authentication, SQL injection, access control, CSRF, XSS) at various stages of completion. This mirrors real-world software development processes, where issues are identified, planned for, fixed, and reviewed iteratively.

## Forking the Project on GitLab

To get started, you will first need to fork the InShare project on GitLab. Visit the project repository on the [UiB GitLab instance](https://git.app.uib.no) and click on the "Fork" button to create your own copy of the project. Once your fork is created, make sure to set the repository to private under the project settings.

You must also give access to the TAs and the lecturer. To do this, go to the "Manage" → "Members" section of your repository and add the teachers as developers:

 - Håkon Gylterud
 - Willem Schooltink
 - Shania Muganga
 - Jonas Haukenes
 - Julie Mikkelsen
 - Endre Sletnes
 - Eivind Sulen

GitLab will be your main platform for collaboration, where you can create branches for working on different parts of the code, open issues to document vulnerabilities, and create merge requests for peer review. Ensure that all members of your group actively contribute by using branches and reviewing each other’s work.

**Remember to add any libraries you want to use to the pom.xml file.**

## Documenting your work

**Fill out the report in the bottom of this page as you go along.** It is divided into sections already with some suggestions what to write.

## Phase 1: Planning mitigations

In the first phase you should plan how to improve the security of InShare. **Write down your planning in the report in the bottom of this page.**


### SQL injection

Plan the mitigation of SQL injection, and create GitLab issues for the fixes. How will you determine/test that the vulnerability is fixed?


### XSS

Plan the mitigation of existing XSS vulnerabilities. The cruicial part to consider is the content of notes.
The solution for text formatting *requires* use of HTML tags in the content of notes.
One possible solution would be to use an HTML Sanitiser (such as [OWASP AntiSamy](https://owasp.org/www-project-antisamy/)).

How will you determine that the vulnerability is fixed?

Create a mitigation plan and GitLab issues for the fixes.

### CSRF

Plan the mitigation of CSRF vulnerabilities in InShare, and create GitLab issues for the fixes.

How will you determine that the vulnerability is fixed?

### Authentication

In the previous assignment we identified weaknesses in the authentication system of InShare.
In particular there is no key derivation function applied to the password before storing it in the database,
and there are no requirements on password lengths.

Focussing on **password storage** using a key derivation function (Argon2 or scrypt are recommended)
and **ensuring user password strength**, create a plan
for improving the authentication system in InShare. Break the plan into GitLab issues.

Some things to consider:

 - Will there be any changes to the UI?
 - What are best practises to encourage users to pick a strong password?
 - How will you determine that the security of the authentication mechanism is improved?

### Access control

The problems with access control in InShare is twopart:

 - Insufficient checks on permissions: Most permissions are only enforced in the UI. Only the
   DELETE permission is checked in the backend.
 - Limited access control model: The access control list method is probably not the best
   fit for the application.

It will be practical to address the second issue first by replacing the access control system,
and then the second one by ensuring that the new access control system verifies all permissions
in the backend.

Plan for the creation of a Role Based Access Control (RBAC) for InShare:

 - Include a new database schema for the roles and permissions. Remember to set up foreign keys, and add additional constraints where suitable.
 - The roles should be:
   - "owner": Each note has a unique owner. Has read/write/delete permissions. Cannot be revoked, only transferred by the owner themselves.
   - "administrator": Has read/write/delete permissions. Can set roles (except owner).
   - "editor": Has read/write permissions.
   - "reader": Can only read the note.
 - Plan which methods on the backend have to include checks for permssions, and how this will be coordinated with the UI.
 - Change the UI so that the sharing mechanism uses the new roles. Include an option to transfer ownership of a note.
 - How will you determine that the security of the access control mechanism has improved?

### Logging

There is currently very little logging going on in InShare. Identify what logging is taking place, and plan the introduction
of more security logging. Make sure that you follow best practises on what to log and what not to log.

Create GitLab issues for adding logging to various parts of the code.

## Phase 2: Implementation

In this phase you will do the actual implementation of the fixes. Make the fixes on separate branches, and follow
the issues you have created. In the report you can mention any particular challenges you had to overcome in the
implementation.

### Impelment protections against SQL injection, XSS, CSRF

Working in separate branches, implement the fixes for SQL injection, XSS and CSRF, according to your plan from the
previous phase. Do not merge into the main branch until another team member has peer-reviewed your code. See next phase.

### Implement improvements to authentication

Working in a seprarate branch, implement the authentication changes planned in phase 1.
Do not merge into the main branch until another team member has peer-reviewed your code. See next phase.

### Implement improvements to access control

Working in a seprarate branch, implement the access control changes planned in phase 1.
Do not merge into the main branch until another team member has peer-reviewed your code. See next phase.

### Implement logging improvements

Working in a seprarate branch, follow the previous laid out plan to implement security logging.
Do not merge into the main branch until another team member has peer-reviewed your code. See next phase.


## Phase 3: Review and testing

**Note**: Even if you are working alone, get someone else from the course to review your code if at all possible.

In this phase you submit a merge request for each of the branches from the previous phase, and peer-review the changes.

 - Remember to test your code before submitting a merge request.
 - Be clear in the merge request what is being implemented, and which issues are affected.

When reviewing think about the following:

 - Focus on security.
 - Check that the code is readable and is clear.
 - Test the code. Checkout the branch and do some manual testing.
 - Be constructive in your feedback! Start by saying something postitive.
 - Verify that the changes addresses the correct issues.

When you are done, make sure that correct issues are closed.


# Report

Here you can document your work.


## SQL Injection Protection (2 pts)

Short description of the issue.

### Planning

Explain the mititgation techiniques for SQL injection which are planning to apply.

Link to issue(s) created.

### Implementation

Describe any challenges you faced in the implementation.

Link to commits which are part of the fix.

### Review

Describe the steps you have taken to ensure that the issue is really fixed.

Link to merge request with review.

## XSS Protection (3 pts)

Short description of the issue.

### Planning

Explain how you plan to mitigate the XSS vulnerability while keeping the formatting functionality.

Link to issue(s) created.

### Implementation

Describe any challenges you faced in the implementation.

Link to commits which are part of the fix.

### Review

Describe the steps you have taken to ensure that the issue is really fixed.

Link to merge request with review.


## CSRF Protection (2 pts)

Short description of the issue.

### Planning

Describe your plan to implement CSRF protection here.

Link to issue(s) created.

### Implementation

Describe any challenges you faced in the implementation.
Link to commits which are part of the fix.

### Review

Describe the steps you have taken to ensure that the issue is really fixed.

Link to merge request with review.


## Authentication Improvement (3 pts)

Short description of the issue.

### Planning

Detail your plan for improving the authentication here.

Link to issue(s) created.

### Implementation

Describe any challenges you faced in the implementation.
Link to commits which are part of the fix.

### Review

Describe the steps you have taken to ensure that the issue is really fixed.

Link to merge request with review.


## Access Control Improvement (4 pts)

Give a short description of the access control vulnerabilities
in InShare.

### Planning

**Identifying the issues**

The current access control model uses a discreationary (to some degree) access control model, the author delegates who should have access to their resources. After the note is shared, any whom has now access to the note can share it further. This is flawed. Only the DELETE action is properly checked at the backend and other actions rely on the UI which is a bad practice. The system also has insecure direct object refrences which can be exploited without permissions, this problem is related to the lack of backend permission checks.

**presented solutions**
Iteration 1: limit sharing to those with write access, perform backend permission checks.
Iteration 2: introduce Role based access control, fix flawed UI.

**Issues IT1**

- Limit share access to users with WRITE access
- Ensure permission checks are handled at backend


**RBAC (from assignment notes)**
Plan for the creation of a Role Based Access Control (RBAC) for InShare:

Include a new database schema for the roles and permissions. Remember to set up foreign keys, and add additional constraints where suitable.
The roles should be:

- "owner": Each note has a unique owner. Has read/write/delete permissions. Cannot be revoked, only transferred by the owner themselves.
- "administrator": Has read/write/delete permissions. Can set roles (except owner).
- "editor": Has read/write permissions.
- "reader": Can only read the note.

Plan which methods on the backend have to include checks for permssions, and how this will be coordinated with the UI.
Change the UI so that the sharing mechanism uses the new roles. Include an option to transfer ownership of a note.
How will you determine that the security of the access control mechanism has improved?



[Link to issue(s) created.](https://git.app.uib.no/Mathias.H.Ness/inshare/-/issues/9)

### Implementation

Describe any challenges you faced in the implementation.
Link to commits which improve the access control system.

**1st iteration (sharing is limited to write access)**
__done:__

- share button removed from from users who does not have write access
- Permissionchecks for all backend notecontroller actions
- in share method only approved permissions will now be appended to user-note-permissions

**RBAC model**
- Impl. RBAC in DB -> `SQLiteConfig.java`. 
- remove old structure in DB -> `SQLiteConfig.java`.
- update UI
- update backend to adapt to new roles.
- enforce backend permission checks on all actions

**implications**
- the sample db is not compatible with the new stucture

### Review

Link to merge request with review.

## Logging System Improvement (1 pts)

Give a short description of the principles behind security logging.


### Planning

Describe what events should be logged, and how you will implement this.

What are you recommendations for log monitoring and response for InShare?

Link to issue(s) created.

### Implementation

Describe any challenges you faced in the implementation.
Link to commits which implement logging.

### Review

Link to merge request with review.

