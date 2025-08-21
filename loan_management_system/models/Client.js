const mongoose = require('mongoose');

const ClientSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'El nombre del cliente es obligatorio.'],
    trim: true,
  },
  documentId: {
    type: String,
    required: [true, 'El ID del documento es obligatorio.'],
    unique: true,
    trim: true,
  },
  documentPhoto: {
    type: String,
    required: [true, 'La foto del documento es obligatoria.'],
  },
  phone: {
    type: String,
    trim: true,
  },
  email: {
    type: String,
    trim: true,
    lowercase: true,
  },
  address: {
    type: String,
    trim: true,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

module.exports = mongoose.model('Client', ClientSchema);
