const express = require('express');
const connectDB = require('./db');

// Connect to Database
connectDB();

const app = express();

// Init Middleware
app.use(express.json({ extended: false }));

// Define a simple root route
app.get('/', (req, res) => res.send('API Running'));

// Define Routes
app.use('/api/clients', require('./routes/clients'));
app.use('/api/loans', require('./routes/loans'));

const PORT = process.env.PORT || 5000;

app.listen(PORT, () => console.log(`Server started on port ${PORT}`));
