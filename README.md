# E2E Mock Server Replay

POC how to run API calls within your application against your local mock server.

This is build upon a Spring boot command line app, but those dependencies are not mandatory in general. 


## Idea

This project has two parts:

1.) Run your app and record requests and response pairs, see [src/main/java/net/mnio/apiclient/RequestResponseLogger.java]()

2.) Use those records within your test to start up a mock server to expect those requests.

This project relies on "Mock Server" (https://www.mock-server.com).


### Good to know

- Records are stored within "mockServerReplays".
- Each request response pair (record) is stored in a single file, named like "request-response-\<start timestamp>-\<diff timestamp>.log"\
  whereas first timestamp is fixed to the current run,\
  second one is the current diff which leads to a strictly increasing counting here
- Records are read __in order and expected to be called exactly once__, see [src/test/java/net/mnio/MockServerBuilder.java]()
- Within records, the URI host is ignored as it is always needed to be the URI of our mock server at http://localhost:1080.
- While running the tests, the API URI to be called need to point to http://localhost:1080 for this reason.

See [src/main/resources/application.yml]() and [src/test/resources/application-test.yml]() for more details.

### How to debug

Check out log files as "Mock Server" is quite chatty what is expected and why it failed.

There is a dashboard available at http://localhost:1080/mockserver/dashboard.
Unfortunately it is only available while the tests are running,
which means if a break point is hit, the dashboard won't load.

This could be circumvented by running the mock server in a different java process, 
but this would lead to a more cluttered project setup here.