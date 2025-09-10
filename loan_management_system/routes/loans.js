const express = require('express');
const router = express.Router();
const { createLoan, getLoans, getLoanById } = require('../controllers/loansController');

// @route   POST api/loans
// @desc    Create a new loan
// @access  Public // TODO: Add auth later
router.post('/', createLoan);

// @route   GET api/loans
// @desc    Get all loans
// @access  Public // TODO: Add auth later
router.get('/', getLoans);

// @route   GET api/loans/:id
// @desc    Get a single loan by its ID
// @access  Public // TODO: Add auth later
router.get('/:id', getLoanById);

module.exports = router;
