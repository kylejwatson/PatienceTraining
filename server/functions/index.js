const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.newRank = functions.firestore
    .document('users/{userId}')
    .onCreate((snap, context) => {
		return totalTimeSet(snap.data(), snap.ref);
	});
// Listen for updates to any `user` document.
exports.orderRank = functions.firestore
    .document('users/{userId}')
    .onUpdate((change, context) => {
		// Retrieve the current and previous value
		const data = change.after.data();
		const previousData = change.before.data();

		// We'll only update if the name has changed.
		// This is crucial to prevent infinite loops.
		if (data.totalTime === previousData.totalTime) return null;

		return totalTimeSet(data, change.after.ref);
    });
	
function totalTimeSet(data, ref){
	let newRank = 1;
	let collectionRef = admin.firestore().collection('users');
	return collectionRef.where('totalTime', '>', data.totalTime)
		.orderBy('totalTime', 'asc')
		.limit(1)
		.get()
		.then(querySnapshot => {
			querySnapshot.forEach(documentSnapshot => {
				newRank = documentSnapshot.get('rank') + 1;
				console.log(`Found document at ${documentSnapshot.ref.path}`);							
			});
			// Then return a promise of a set operation to update the count
			return ref.set({
				rank: newRank
			}, {merge: true});
		});
}	
	
exports.moveRankUp = functions.firestore
	.document('users/{userId}')
	.onDelete((snap, context) => {
		let collectionRef = admin.firestore().collection('users');
		return collectionRef.where('totalTime', '<', snap.data().totalTime)
			.get()
			.then(querySnapshot => {
				querySnapshot.forEach(documentSnapshot => {
					newRank = documentSnapshot.get('rank') - 1;
					documentSnapshot.ref.set({
						rank: newRank
					}, {merge: true});
				});
				return null;
			});
	});
	
exports.moveRankDown = functions.firestore
	.document('users/{userId}')
	.onUpdate((change,context) => {
		const data = change.after.data();
		const previousData = change.before.data();
		console.log(`Found data ${data.rank}`);	
		console.log(`Found data ${previousData.rank}`);			
		if(data.rank === previousData.rank) return null;
		
		let collectionRef = admin.firestore().collection('users');
		return collectionRef.where('rank', '==', data.rank)
			.get()
			.then(querySnapshot => {
				querySnapshot.forEach(documentSnapshot => {
					
					if(documentSnapshot.id !== change.after.ref.id){
						console.log(`Found user at ${documentSnapshot.id} is not user at ${change.after.ref.id}`);	
						//console.log(`Found is nuser at ${documentSnapshot.id}`);							
						//let userRef = collectionRef.doc(documentSnapshot.id);
						let newRank = documentSnapshot.get('rank') +1
						return documentSnapshot.ref.set({
							rank: newRank
							}, {merge: true});
					}
					return null;
				});
					
				return null;
			});		
	});
