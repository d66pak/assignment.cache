assignment.cache
================

assignment work to develop in memory cache in java 1.7


DESIGN
======

This Caching application is designed to support various caching types like in-memory, file based, etc. In addition, it
is also carefully designed to support caching algorithms like timed cache, LRU, etc.

The design is flexible enough to add various caching types or algorithms in future. At present, this implementation supports;
in-memory caching and timed cache algorithm where cache values are removed after user specified TTL has elapsed.

NOTE: Please read through the class/method specific javadoc comments to understand the design better.

A brief summary about the various classes:

+ CacheBuilder -> Builder for creating CacheManager based on various configuration parameters

+ CacheManager interface -> Responsible for managing access & modification to the cache, encapsulating value into CacheObject and
purging/cleaning the cache

+ AbstractCacheManager -> Skeletal implementation of CacheManager

+ InMemoryCacheManager -> In memory cache manager implementation

+ Cache interface -> Actual cache where values are stored

+ InMemoryCache -> In memory cache implementation. Uses ConcurrentHashMap to store values

+ CacheObject interface -> Values are encapsulated within cache object. Various caching algorithms can be supported by
implementing this interface

+ TimedCacheObject -> Timed caching implementation of CacheObject

TESTING
=======

TestNG framework is used for testing this solution. It would be easy to understand the solution by analysing the test cases.

NOTE:

+ Test cases are written for CacheBuilder and a case for load testing is also added
+ Test cases for straight forward classes are not added

BUILD
=====

+ Apache Maven is used as a project management/build tool
+ Use 'mvn compile test' command to compile and run the tests

ASSUMPTIONS
===========

+ In memory cache
+ The system has plenty of memory
+ The TTL of the items is fairly short (~10 seconds)
+ The keys are widely distributed across the key space
+ Each item may or may not be accessed during the TTL
+ Each item might be accessed multiple times during the TTL
