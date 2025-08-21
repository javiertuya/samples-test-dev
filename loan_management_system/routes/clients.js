const express = require('express');
const router = express.Router();
const multer = require('multer');
const { createClient } = require('../controllers/clientsController');

// Configure Multer for file storage
const storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/'); // Make sure this directory exists
  },
  filename: function (req, file, cb) {
    // Create a unique filename to avoid overwriting
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
    cb(null, file.fieldname + '-' + uniqueSuffix + '.' + file.originalname.split('.').pop());
  }
});

const upload = multer({ storage: storage });

// @route   POST api/clients
// @desc    Create a new client
// @access  Public // TODO: Add auth later
router.post('/', upload.single('documentPhoto'), createClient);

module.exports = router;
