const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Listen for updates to any `user` document.
exports.setNewRank = functions.firestore
    .document('users/{userId}')
    .onUpdate((change, context) => {
      // Retrieve the current and previous value
      const data = change.after.data();
      const previousData = change.before.data();

      // We'll only update if the name has changed.
      // This is crucial to prevent infinite loops.
      if (data.totalTime === previousData.totalTime) return null;
	  let newRank = 1;
	  let collectionRef = admin.firestore().collection('users');
	  return collectionRef.where("totalTime", "<", previousData.totalTime)
					.limit(1)
					.get()
					.then(querySnapshot => {
						querySnapshot.forEach(documentSnapshot => {
							newRank = documentSnapshot.get('rank');
							console.log(`Found document at ${documentSnapshot.ref.path}`);							
						});
						// Then return a promise of a set operation to update the count
						return change.after.ref.set({
							rank: newRank
						}, {merge: true});
					});
    });
