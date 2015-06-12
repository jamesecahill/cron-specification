## Cron-Specification

Simple utility for creating and validating target dates and times for columnular cron specifications.

See tests for example usages.

```
* * * * *
│ │ │ │ │
│ │ │ │ │
│ │ │ │ └───── day of week (0 - 6) (0 to 6 are Sunday to Saturday)
│ │ │ └────────── month (1 - 12)
│ │ └─────────────── day of month (1 - 31)
│ └──────────────────── hour (0 - 23)
└───────────────────────── min (0 - 59)
```

### Pre-reqs

 * Java 1.8
 * Maven

### Contributing

 * Tests are appreciated
 * Fork, PR, Lather, Rinse, Repeat
