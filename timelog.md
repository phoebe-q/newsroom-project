# Timelog

- Newsroom: A news aggregation tool
- Phoebe Quinn
- 2305116Q
- Richard McCreadie

## Guidance

- This file contains the time log for your project. It will be submitted along with your final dissertation.
- **YOU MUST KEEP THIS UP TO DATE AND UNDER VERSION CONTROL.**
- This timelog should be filled out honestly, regularly (daily) and accurately. It is for _your_ benefit.
- Follow the structure provided, grouping time by weeks. Quantise time to the half hour.

## Week 1

### 1 Oct 2021

- _0.5 hour_ Met with supervisor and decided basics of project, talk about a few different approaches with the intention to decide by next meeting

### 4 Oct 2021

- _2 hours_ Researched related products for possible approaches to project
- _1 hour_ Read the project guidance notes
- _1.5 hours_ Researched different technologies and read docs to get an idea of what should be used.

## 6 Oct 2021

- _4 hours_ Decided on a news based system, looked more detailed into related systems - set up accounts and browsed to get an idea of what was already available.

## 8 Oct 2021

- _0.5 hours_ Met with supervisor and discussed more about the project. Discussed ideas for news based system.

## Week 2

### 15 Oct 2021
- _0.5 hour_ Met with supervisor and discussed basics more discussed using Play as a possible technology. Call was interrupted due to wifi problems.
- _4 hour_ Researched topic modelling for implementation

### 16 Oct 2021
- _3 hours_ researched web scraping for articles and attempted to write basic code to do this

### 17 Oct 2021
- _7 hours_ continued trial of web scraping

## Week 3

### 19 Oct 2021

- _3 hours_ Discarded original approach to scraping and trialed new attempt
## 20 Oct 2021

- _3.5 hours_ Final attempt at scraping - decided it was outwith the project scope on this project and would use dataset instead

### 22 Oct 2021

- _0.5 hour_ Met with supervisor to discuss scraping and using dataset

## Week 4

### 25 Oct 2021

- _2.5 hours_ Read again about topic modelling

### 26 Oct 2021

- _3 hours_ Read about issues current consumers feel with the news online
- _3 hours_ Planned general ideas for the system

## 29 Oct 2021

- _0.5 hours_ Met with supervisor discussed findings and next steps

## 30 Oct 2021

- _4.5 hours_ Created user personas for the project and use cases

## Week 5

### 1 Nov 2021

- _3 hours_ Created requirements list for project and sorted into MoSCoW format
- _4 hours_ Researched Architecture Diagrams and created my own basic one

### 2 Nov 2021

- _4 hours_ designed several different approaches to the system on paper prototypes
- _2 hours_ gained advice from friends on the best design approach

## 4 Nov 2021

- _1 hour_ looked into best design tools to use

## 5 Nov 2021
- _0.5 hours_ met with supervisor for weekly meeting
## Week 6

### 8 Nov 2021

- _0.5 hour_ Met with supervisor discussed design stage
- _2 hours_ started designs using draw.io but was too basic so decided to use Figma instead

### 9 Nov 2021
- _3 hours_ Created basic designs in Figma

### 10 Nov 2021
- _2 hours_ made designs more detailed in Figma



## Week 7

### 15 Nov 2021
- _3 hours_ completed Figma designs

### 16 Nov 2021

- _0.5 hours_ Met with supervisor for weekly meeting discussed designs and further progress

### 18 Nov 2021
- _3.5 hours_ read up on how play is structured and decided to start by implementing views

## Week 8

### 22 Nov 2021

- _4 hours_ Worked on basic view implementation - stall: couldn't get the view linking to work messaged Richard, awaiting response. The struggle was that the search() function won't call.
- _1 hour_ Refactored Architecture Diagram to suit NewsRoom and be more specific.

### 23 Nov 2021

- _1 hour_ Tried a new approach to view navigation from Play Framework examples - did not work. Decided to keep in and wait for supervisor advice
- _0.5 hours_ Met with supervisor and got advice with view navigation
- _1 hour_ Fixed basic view navigation - can navigate between pages now have to add variable to pass
- _1.5 hours_ Tried to add in variable to pass to Results view - this resulted in an error so spent a while trying to fix. There is almost no info on the internet so process went very slowly.

## Week 9
- Illness this week stalled development

### 29 Nov 2021

- _4 hours_ tried again with passing variables between views - from research online it should be working

### 30 Nov 2021

- _3 hours_ looked at alternative structures online to stop the issue with passing between views


## Week 10

- no development this week due to exams

## Week 11

### 13 Dec 2021
- _2 hours_ researched datasets
- _2 hours_ selected BBC dataset and explored to understand how it was structured

## Week 12

- Christmas stalled development this week

### 20 Dec 2021
- _2 hours_ reread previous research I had done to familiarise with frameworks
- _3 hours_ created MySql db for use with ES

## Week 13

### 28 Dec 2021

- _4 hours_  Populated MySql Database - did this by writing a python script which took the content from the local files 
- _1 hour_ Began to follow EBean linking tutorial to set up models

### 29 Dec 2021

- _2.5 hours_  Had dependency issues with Ebean, spent a long time working out what exactly was wrong and correcting the error


### 2 Jan 2022

- _4 hours_  Populated MySql Database - did this by writing a python script which took the content from the local files. 
- _1 hour_ Began to follow EBean linking tutorial to set up models


## Week 14

### 3 Jan 2022

- _3 hours_ Continued to try to link Ebean - realised it was not necessary for project so discarded
- _3 hours_ Attempted to link MySql database with ElasticSearch instance, found this needlessly difficult so decided to populate the ElasticSearch instance rather than trying to link


### 4 Jan 2022

- _1 hour_ Wrote Java file to populate the ElasticSearch instance 
- _1 hour_ Began to write code to search ElasticSearch instance
- _2 hours_ Continued writing the search function. This was stalled due to documentation I was following being out of data for my Java version.
- _1 hour_ Researched different approaches to search - Richard suggested a new approach which I decided on
- _2 hours_ Implemented 


### 6 Jan 2022

- _2 hours_ Tried to fix search functionality on my own - application behaving in a way that is not logical so could not understand what was wrong in order to fix. 

### 7 Jan 2022

- _4 hours_ again attempted to fix search functionality to no avail

### 8 Jan 2022

- _5 hours_ noticed issues with ElasticSearch population - tried to fix but couldn't


## Week 15

### 10 Jan 2022

- _0.5 hours_ Began Dissertation: Loaded on overleaf, wrote title page, rough abstract and dedications
- _1.5 hours_ Fixed ElasticSearch population; the issue was that the text being read in was being read in as a stream not a String. Search still did not return correctly, began to examine the search algorithm
- _1 hour_ Fixed ElasticSearch search, search still not totally working as term does not pass from index view to results view.
- _1 hour_ Fixed the passing of the term to search, then tidied up the way the data is stored. Added title field to the database to make parsing easier
- _0.5 hours_ Re-read Youtube API docs to prepare to begin working on the Youtube call implementation
- _2.5 hours_ Worked on implementing results view properly, listing each article that is returned in the search. This was stalled as you cannot pass a List in routes without a QueryPathBindable, had to implement one of these - this took a large portion of time as documentation was not completlely clear.

### 11 Jan 2022

- _2 hours_ Worked on implementing Youtube Search. Found and imported relevant JAR files - stalled on error due to conflicting types.
- _0.5 hours_ Met with Richard to discuss planning: decided to work on Youtube Search in tandem with css formatting of code. Also discussed how best to begin dissertation, Richard advised begging with background as it will help with both requirements and introduction
- _0.5 hours_ Looked into using bootstrap with play

### 12 Jan 2022

- _1.5 hours_ Worked on implementing Bootstrap: downloaded Bootstrap and JQuery and imported neccessary parts into project.

### 13 Jan 2022

- _3.5 hours_ Worked on implementing Bootstrap: downloaded and implemented theme, made adjustments. Was blocked by theme import issues, but managed to solve in a couple of hours

### 15 Jan 2022

- _3.5 hours_ Worked on implementing Bootstrap: updated to Bootstrap v5 and made changes needed for new version to run, added css to results page

### 16 Jan 2022

- _3.5 hours_  Worked on implementing Bootstrap: fixed some errors, began creating page for single result view. Struggled with implementing pathbindable for lone article


## Week 16

### 17 Jan 2022

- _1 hour_  Worked on implementing Bootstrap: continued to struggle with implementing path binder, will speak to Richard about it and out next meeting tomorrow
- _2 hours_ Tried to implement YouTube search, blocked by waiting on API permissions to update on google developer
- _1 hour_ Began creating ArticleActor to handle transfer of data between views in project

### 18 Jan 2022

- _2 hours_  Tried to fix websocket to be able to pass data between views.
- _0.5 hours_ Met with Richard, he advised I put all the data on one page to avoid having to pass between views, we also discussed topic modelling and sorting of data
- _1 hour_ Tried to fix YouTube Call - continues to break with a 400 error

### 19 Jan 2022

- _2 hours_ Worked on fixing YouTube Call - I am blocked in this area as I have tried multiple solutions and none of them seem to work
- _1 hour_  Started on updates to bootstrap Richard recommended

### 20 Jan 2022

- _3 hours_ Continued working on disappearing navbar, went back and forth for a while. Nearly working
- _1 hour_ Tried to fix YouTube search - callback now targeting correct port, but same error still occurring 

### 22 Jan 2022

- _1 hour_ Began with mallet, downloaded the package and unpacked. Read docs so I was prepared for the next day.

### 23 Jan 2022

- _3 hours_ Got mallet topic modelling linked into project code and working, returns 5 topics for each search
- _1 hour_  Fixed css side menu - now hides to the side on click
- _1.5 hours_ fed topics into results page

### 24 Jan 2022

- _2 hours_ Fixed YouTube search, beginning now to pull out subtitles to pass to view

## Week 17

### 25 Jan 2022

- _4 hours_ Fixed YouTube Search, Caption download presented some issues however, seems that I may need to find a work-around as it may not be possible to download captions using api
- _1 hour_ Updated topic sidebar to include all topics

### 26 Jan 2022

- _1 hour_ read up about similarity sorting contents by topic
- _1 hour_ implemented spinner to visualise loading on index page
- _0.5 hours_ Fixed bug in sidebar to make click of any item in menu hide it.

### 27 Jan 2022

- _3 hours_ Tried to implement Subtitles work around, however GET requests returns nothing - think this method has deprecated

## Week 18

### 31 Jan 2022
- _1.5 hours_ Researched other packages that allow retrieval of subtitles and discovered YoutubeDL for Java.
- _1 hour_ Implemented YouTubeDL, however subtitles have to be written and read from file: will attempt to find workaround

## Week 19

### 7 Feb 2022
- _3 hours_ stalled dev due to errors passing data

## Week 20

### 14 Feb 2022
- _2 hours_ refactored display of articles

### 15 Feb 2022
- Had to move weekly meeting with Richard due to hurting my back and being unable to sit or stand - also stalled development by a couple of days.

### 18 Feb 2022
- _1 hour_ - Met with Richard for our weekly session - we discussed changing the application to be single page to make processes more visible to the user.

## Week 21
### 21 Feb 2022
- _4 hours_ refactored designs to suit this one-page version of the system

### 22 Feb 2022
- _4 hours_ refactored stucture of application to include article search, youtube search and topic modelling as events

## Week 22

### 28 Feb 2022
- _3 hours_ refactored topic modelling to be more modular
- _1 hour_ refactored search of articles
- _2 hours_ refactored search of youtube and downloading of subtitles to be more modular

### 1 March 2022
- _3 hours_ discovered bugs in both topic modelling and subtitles - debugged to attempt to understand why this was happening

### 5 March 2022
- _1 hour_ - Fixed issue with subtitles not matching the correct video. Had to make sure all files deleted after use and changed .vtt file to have title of videoID rather than integer to make it easier to match.
- _3 hours_ - Fixed .vtt file parsing using regex to make sure none of the timings or extra details are included in the parsed text used in cards.
- _0.5 hours_ - Refactored code base to remove unneeded views, routes and classes in controllers
- _3.5 hours_ - Fixed topic modelling issue of sorted text not matching correct topic
- _1 hour_ - Added images to article view, tried to repopulate database with newline between paragraphs did not work will have to try a different approach

### 6 March 2022
- _5 hours_ conducted pilot tests with a group of participants - went very slowly as it was my first time implementing a testing procedure

## Week 23

### 8 March 2022
- _2 hours_ Analysed data returned from pilot tests to create final changes to the system.
- _0.5 hours_ Met with supervisor and discussed testing and changes, supervisor advised I make changes quickly in order to get everything done in time for the deadline

### 9 March 2022
- _3 hours_ Refactored the design requested changes from pilot study
- _1 hour_ Refactored search to search "title" field as it produced better results, trialed this several times
- 
## Week 24

### 14 March 2022
- _3 hours_ Began writing dissertation started with Chapter 1 and wrote aims and motivation


### 15 March 2022
- _0.5 hours_ Met with supervisor and  discussed that I would be very busy this week with hand-ins from other courses but next week all of my time would be devoted to the project 

### 16 March 2022
- _2 hours_ collected designs and other images I would need to the project in one place to make writing easier

## Week 25
- It should be noted that anxiety - the reason I received a week extension was particularly bad this week and delayed a lot of things being completed
### 22 March 2022
- _0.5 hours_ met with supervisor, expressed concern about completion due to anxiety and discussed my meeting with student wellbeing to discuss an extension
### 23 March 2022
- _3 hours_ wrote background section for dissertation including related products
### 24 March 2022
- _3 hours_ wrote background section for dissertation including related products

### 25 March 2022
- _2 hours_ looking for the folder I had put my paper prototypes into at the start of the project

### 26 March 2022
- _6 hours_ wrote design section for dissertation
- _3 hours_ outlined implementation section

### 27 March 2022
- _8 hours_ wrote implementation section

## Week 26

### 29 March 2022
- _0.5 hours_ met with supervisor who read over what was already in dissertation - provided feedback that more detail was needed in almost every section
- _0.5 hours_ created to do list from supervisor comments.

### 31 March 2022
- _1 hour_ conducted first full experiment on first participant
- _6 hours_ refactoring dissertation to be more detailed as supervisor suggested
- _1 hour_ conducted second full experiment on second participant

### 1 April 2022
- _2 hours_ added additional sections to background, design and implementation as advised by supervisor
- _1 hour_ researched how to test performance of a system in dev tools and discovered Lighthouse
- _1 hour_ downloaded and used lighthouse on the Newsroom site.

### 2 April 2022
- _5.5 hours_ conducted final experiment on remaining 8 participants

## Week 27

### 4 April 2022
- _8 hours_ Wrote analysis chapter, beginning with results of testing, moving onto lighthouse results and then requirements
- _2 hours_ Wrote conclusion about success of project and possible future work

### 5 April 2022
- _7 hours_ Created presentation about the system
- _2 hours_ Wrote speech notes for presentation
- _2 hours_ Attempted to record presentation, tools used cut audio in and out and mistakes kept being made

### 6 April 2022
- _1 hour_ got presentation recorded, but it was well overtime at 25 minutes, cut down to make shorter
- _1 hour_ recorded presentation again, but again it was over time at 20 minutes so cut down again
- _0.5 hours_ recorded presentation and it was 14 minutes, watched through and everything worked
- _1 hour_ realised presentation video had to be converted to mp4, found a site to do this, downloaded and watched again to make sure it was correct

### 7 April 2022

- _6 hours_ read through dissertation and refactored to tighten up
- _1 hour_ downloaded testing results to get ready for submission
- _1 hour_ added appendices to dissertation

### 8 April 2022

- _2 hours_ final read through of dissertation and clean up
- _1.5 hours_ final read through of code and clean up
- _1 hour_ format for submission