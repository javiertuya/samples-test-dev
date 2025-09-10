const mongoose = require('mongoose');

const LoanSchema = new mongoose.Schema({
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Client',
    required: true,
  },
  amount: {
    type: Number,
    required: [true, 'El monto del préstamo es obligatorio.'],
  },
  interestRate: {
    type: Number,
    required: [true, 'La tasa de interés es obligatoria.'],
  },
  totalAmount: {
    type: Number,
    required: true,
  },
  installments: {
    type: Number,
    required: [true, 'El número de cuotas es obligatorio.'],
  },
  installmentAmount: {
    type: Number,
    required: true,
  },
  period: {
    type: String,
    enum: ['daily', 'weekly'],
    required: [true, 'El período (diario/semanal) es obligatorio.'],
  },
  issueDate: {
    type: Date,
    default: Date.now,
  },
  status: {
    type: String,
    enum: ['active', 'paid'],
    default: 'active',
  },
});

module.exports = mongoose.model('Loan', LoanSchema);
