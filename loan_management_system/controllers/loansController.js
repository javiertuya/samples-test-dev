const Loan = require('../models/Loan');
const Client = require('../models/Client');
const Payment = require('../models/Payment');

// @desc    Create a new loan and its payment schedule
// @route   POST /api/loans
exports.createLoan = async (req, res) => {
  const { client: clientId, amount, interestRate, installments, period } = req.body;

  try {
    // 1. Validate client exists
    const client = await Client.findById(clientId);
    if (!client) {
      return res.status(404).json({ msg: 'Cliente no encontrado.' });
    }

    // 2. Loan Calculation
    const interest = (amount * interestRate) / 100;
    const totalAmount = Number(amount) + interest;
    const installmentAmount = totalAmount / installments;

    // 3. Create the Loan document
    const loan = new Loan({
      client: clientId,
      amount,
      interestRate,
      totalAmount,
      installments,
      installmentAmount,
      period
    });

    await loan.save();

    // 4. Generate Payment Schedule
    const payments = [];
    let currentDate = new Date();

    for (let i = 1; i <= installments; i++) {
      let dueDate = new Date(currentDate);
      if (period === 'daily') {
        dueDate.setDate(currentDate.getDate() + i);
      } else if (period === 'weekly') {
        dueDate.setDate(currentDate.getDate() + (i * 7));
      }

      const payment = new Payment({
        loan: loan._id,
        dueDate,
        amount: installmentAmount,
      });
      payments.push(payment);
    }

    // Insert all payment documents into the database
    await Payment.insertMany(payments);

    res.status(201).json(loan);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Error del servidor');
  }
};

// @desc    Get all loans
// @route   GET /api/loans
exports.getLoans = async (req, res) => {
  try {
    // Populate client details in the loan list
    const loans = await Loan.find().populate('client', 'name documentId');
    res.json(loans);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Error del servidor');
  }
};


// @desc    Get a single loan by ID with all its payments
// @route   GET /api/loans/:id
exports.getLoanById = async (req, res) => {
  try {
    const loan = await Loan.findById(req.params.id).populate('client');
    if (!loan) {
      return res.status(404).json({ msg: 'Préstamo no encontrado.' });
    }

    const payments = await Payment.find({ loan: req.params.id }).sort({ dueDate: 'asc' });

    res.json({ loan, payments });
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Error del servidor');
  }
};
