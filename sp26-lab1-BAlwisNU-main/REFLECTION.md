# Reflection

Answer the following questions in 1-3 sentences each. Your answers should reflect your experience completing this lab.

## 1. Setup Experience

How did the Java 21 installation process go? Did you encounter any issues? If so, how did you resolve them?

**Your answer:**

I had Java 17 running on my Ubuntu machine. I confirmed it by running  readlink -f $(which javac), Then I installed Java 21 along side it and didn't have any issues.

## 2. IDE Navigation

Which VS Code navigation shortcuts did you try (Go to Definition, Find All References, Show Type Hierarchy, etc.)? Which ones do you think you'll use most often?

**Your answer:**

I tried them to learn the use of the shortcuts. Go To Line (Ctrl + G) was the most useful. The other most useful shortcut was Ctrl + Shift + O to find different methods in the code.

## 3. Static Analysis Tools

This project uses Error Prone and NullAway to catch bugs at compile time. How do these tools differ from runtime errors? Why might catching issues at compile time be valuable?

**Your answer:**

Error Prone and NullAway  operate at compile time analyzing source code without executing them as done by runtime error detection. Catching bugs during compile time is valuable since they are caught before reaching production (reduces production risk) and as a result, stronger than catching them during testing since catching bugs during testing is dependant on data sample choices.


## 4. Git Workflow

Describe your experience using Git in this lab. Did you use the command line, VS Code's Source Control panel, or both? Any challenges?

**Your answer:**

I used both. I had issues during cloning. Even though I could see the repo on the browser, it kept complaing that the repo does not exist. I created a new SSH key and stored it on GitHub and then did the cloning via the command line. However to add, commit and push I was able to use VS Code's Source Control panel without any problems.

## 5. Overall Experience

What's one thing you learned or found interesting in this lab? Is there anything you'd like to explore further?

**Your answer:**

This was the first time I used VS Code to work with a GitHub repo and I found it very interesting since it showed me hw easy it is to manage large code bases in a development environment.
