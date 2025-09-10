const Client = require('../models/Client');

// @desc    Create a new client
// @route   POST /api/clients
exports.createClient = async (req, res) => {
  const { name, documentId, phone, email, address } = req.body;

  // Check if a file was uploaded
  if (!req.file) {
    return res.status(400).json({ msg: 'La foto del documento es obligatoria.' });
  }

  try {
    // Check if client already exists
    let client = await Client.findOne({ documentId });
    if (client) {
      return res.status(400).json({ msg: 'Un cliente con este ID de documento ya existe.' });
    }

    // Create a new client instance
    client = new Client({
      name,
      documentId,
      phone,
      email,
      address,
      documentPhoto: req.file.path // Save the path of the uploaded file
    });

    // Save the client to the database
    await client.save();

    res.status(201).json(client);
  } catch (err) {
    console.error(err.message);
    res.status(500).send('Error del servidor');
  }
};
