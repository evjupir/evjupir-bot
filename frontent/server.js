const app = require('express')()

app.use(require('express').static('.'))
app.listen(80)