const mongoose = require('mongoose');

// TODO: Replace this with your actual MongoDB Atlas connection string.
const MONGO_URI = 'mongodb+srv://<username>:<password>@cluster0.mongodb.net/loan_management_db?retryWrites=true&w=majority';

const connectDB = async () => {
  try {
    await mongoose.connect(MONGO_URI, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log('MongoDB Connected...');
  } catch (err) {
    console.error(err.message);
    // Exit process with failure
    process.exit(1);
  }
};

module.exports = connectDB;
