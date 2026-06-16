db = db.getSiblingDB('cohab_chat');

db.createUser({
  user: 'magofrays',
  pwd: 'labubu',
  roles: [
    { role: 'readWrite', db: 'cohab_chat' }
  ]
});
