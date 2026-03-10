package com.firstaidnow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.firstaidnow.data.local.dao.FirstAidDao
import com.firstaidnow.data.local.entity.FirstAidTopic
import com.firstaidnow.data.local.entity.QuizQuestion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [FirstAidTopic::class, QuizQuestion::class],
    version = 1,
    exportSchema = false
)
abstract class FirstAidDatabase : RoomDatabase() {

    abstract fun firstAidDao(): FirstAidDao

    companion object {
        @Volatile
        private var INSTANCE: FirstAidDatabase? = null

        fun getInstance(context: Context): FirstAidDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FirstAidDatabase::class.java,
                    "firstaidnow_db"
                )
                    .addCallback(PrepopulateCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PrepopulateCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateTopics(database)
                    populateQuiz(database)
                }
            }
        }

        private fun populateTopics(db: FirstAidDatabase) {
            val dao = db.firstAidDao()
            val topics = listOf(
                // BURNS
                FirstAidTopic(
                    category = "Burns",
                    title = "Minor Burns (1st Degree)",
                    icon = "ic_burns",
                    summary = "Treat minor burns affecting only the outer layer of skin with redness and pain.",
                    steps = """["Cool the burn under cool (not cold) running water for at least 10 minutes.","Remove any jewelry or tight clothing near the burned area before swelling starts.","Apply aloe vera gel or an antibiotic ointment to the affected area.","Cover the burn loosely with a sterile, non-stick bandage.","Take an over-the-counter pain reliever like ibuprofen if needed.","Do NOT apply ice, butter, or toothpaste to the burn."]""",
                    warnings = """["Seek medical help if the burn covers a large area or is on the face, hands, feet, or joints.","Watch for signs of infection: increased pain, redness, swelling, or fever."]""",
                    severity = "low"
                ),
                FirstAidTopic(
                    category = "Burns",
                    title = "Severe Burns (2nd/3rd Degree)",
                    icon = "ic_burns",
                    summary = "Serious burns with blistering or charred skin require immediate medical attention.",
                    steps = """["Call 911 immediately for severe burns.","Do NOT remove burned clothing stuck to the skin.","Cover the area loosely with a cool, moist sterile bandage or clean cloth.","Elevate the burned area above heart level if possible.","Watch for signs of shock: pale skin, weakness, rapid pulse.","Do NOT apply ointments, ice, or immerse large burns in water.","Keep the person warm with a blanket over unburned areas."]""",
                    warnings = """["Do NOT break blisters as this increases infection risk.","Chemical and electrical burns always need emergency medical care.","Inhalation burns from smoke require immediate 911 call."]""",
                    severity = "critical"
                ),
                // BLEEDING
                FirstAidTopic(
                    category = "Bleeding",
                    title = "Minor Cuts and Scrapes",
                    icon = "ic_bleeding",
                    summary = "Small wounds that bleed lightly and can be treated at home.",
                    steps = """["Wash your hands thoroughly before treating the wound.","Rinse the wound gently under clean running water.","Apply gentle pressure with a clean cloth or bandage to stop bleeding.","Apply a thin layer of antibiotic ointment.","Cover with a sterile adhesive bandage or gauze.","Change the bandage daily and whenever it gets wet or dirty."]""",
                    warnings = """["See a doctor if the wound is deep, jagged, or won't stop bleeding after 10 minutes.","Get a tetanus shot if you haven't had one in the past 5 years and the wound is deep or dirty."]""",
                    severity = "low"
                ),
                FirstAidTopic(
                    category = "Bleeding",
                    title = "Severe Bleeding",
                    icon = "ic_bleeding",
                    summary = "Heavy, uncontrollable bleeding that requires immediate emergency response.",
                    steps = """["Call 911 immediately.","Put on gloves if available to protect yourself.","Apply firm, direct pressure to the wound using a clean cloth or bandage.","If blood soaks through, add more layers on top — do NOT remove the first cloth.","If bleeding is from an arm or leg, elevate the limb above the heart.","Apply a tourniquet above the wound ONLY as a last resort if direct pressure fails.","Keep the person lying down, warm, and calm until help arrives."]""",
                    warnings = """["Do NOT remove any objects embedded in the wound.","A tourniquet should only be used in life-threatening situations.","Watch for signs of shock: pale skin, rapid breathing, confusion."]""",
                    severity = "critical"
                ),
                FirstAidTopic(
                    category = "Bleeding",
                    title = "Nosebleed",
                    icon = "ic_bleeding",
                    summary = "Common nosebleeds that can usually be managed at home.",
                    steps = """["Sit upright and lean slightly forward (not backward).","Pinch the soft part of the nose just below the bridge firmly.","Hold pressure continuously for 10–15 minutes without checking.","Breathe through your mouth during this time.","Apply a cold compress or ice pack to the bridge of the nose.","After bleeding stops, avoid blowing your nose for several hours."]""",
                    warnings = """["Seek medical help if bleeding lasts more than 20 minutes.","Get emergency help if the nosebleed follows a head injury."]""",
                    severity = "medium"
                ),
                // CHOKING
                FirstAidTopic(
                    category = "Choking",
                    title = "Choking in Adults",
                    icon = "ic_choking",
                    summary = "When an adult's airway is partially or fully blocked by a foreign object.",
                    steps = """["Ask the person 'Are you choking?' — if they cannot speak, cough, or breathe, act immediately.","Stand behind the person and wrap your arms around their waist.","Make a fist with one hand and place it just above the navel (belly button).","Grasp your fist with your other hand.","Perform quick, inward and upward thrusts (Heimlich maneuver).","Repeat thrusts until the object is expelled or the person can breathe.","If the person becomes unconscious, lower them to the ground and begin CPR."]""",
                    warnings = """["Call 911 if the object is not dislodged after several attempts.","For pregnant women or obese individuals, perform chest thrusts instead of abdominal thrusts.","Always recommend medical evaluation after a choking incident."]""",
                    severity = "critical"
                ),
                FirstAidTopic(
                    category = "Choking",
                    title = "Choking in Infants (Under 1 Year)",
                    icon = "ic_choking",
                    summary = "Special technique required for infants who are choking.",
                    steps = """["Hold the infant face-down along your forearm, supporting the head.","Give 5 firm back blows between the infant's shoulder blades using the heel of your hand.","Turn the infant face-up, supporting the head and neck.","Give 5 chest thrusts using two fingers on the center of the breastbone, just below the nipple line.","Repeat cycles of 5 back blows and 5 chest thrusts until the object is dislodged.","If the infant becomes unconscious, begin infant CPR and call 911."]""",
                    warnings = """["Do NOT perform abdominal thrusts (Heimlich) on infants.","Do NOT do a blind finger sweep — only remove objects you can clearly see.","Call 911 immediately if the infant stops breathing."]""",
                    severity = "critical"
                ),
                // CPR
                FirstAidTopic(
                    category = "CPR",
                    title = "Adult CPR (Hands-Only)",
                    icon = "ic_cpr",
                    summary = "Cardiopulmonary resuscitation for adults who collapse and stop breathing.",
                    steps = """["Check the scene for safety before approaching.","Tap the person's shoulders and shout 'Are you OK?'","If no response, call 911 immediately (or have someone call).","Place the person on their back on a firm, flat surface.","Place the heel of one hand on the center of the chest (on the breastbone).","Place your other hand on top, interlocking your fingers.","Push hard and fast — at least 2 inches deep, at a rate of 100–120 compressions per minute.","Allow the chest to fully recoil between compressions.","Continue until emergency services arrive or the person starts breathing.","If an AED is available, use it as soon as possible — follow the voice prompts."]""",
                    warnings = """["Do NOT stop CPR unless the person starts breathing, a trained responder takes over, or you are too exhausted to continue.","Hands-only CPR is recommended for untrained bystanders.","Push to the beat of 'Stayin Alive' by the Bee Gees for the correct rate."]""",
                    severity = "critical"
                ),
                FirstAidTopic(
                    category = "CPR",
                    title = "Child CPR (Ages 1–8)",
                    icon = "ic_cpr",
                    summary = "Modified CPR technique for children between 1 and 8 years old.",
                    steps = """["Check the scene for safety.","Tap the child and shout to check responsiveness.","If no response, have someone call 911. If alone, perform CPR for 2 minutes before calling.","Place the child on their back on a firm surface.","Use one hand (or two for larger children) on the center of the chest.","Compress about 2 inches deep at 100–120 compressions per minute.","After 30 compressions, tilt the head back gently and lift the chin.","Give 2 rescue breaths — each breath should make the chest rise visibly.","Continue cycles of 30 compressions and 2 breaths.","Use an AED with pediatric pads if available."]""",
                    warnings = """["Use less force than for adults — compress about one-third the depth of the chest.","Be gentle when tilting the head to open the airway.","Do NOT delay CPR to call 911 if you are alone — do 2 minutes of CPR first."]""",
                    severity = "critical"
                ),
                // FRACTURES
                FirstAidTopic(
                    category = "Fractures",
                    title = "Suspected Broken Bone",
                    icon = "ic_fractures",
                    summary = "How to stabilize and manage a suspected fracture before medical help arrives.",
                    steps = """["Call 911 if the fracture is severe, involves the spine/neck/hip, or the person cannot move.","Keep the injured area as still as possible — do NOT try to realign the bone.","Apply ice wrapped in a cloth to reduce swelling (20 minutes on, 20 minutes off).","Immobilize the injury with a makeshift splint if you have training (use stiff material and padding).","Elevate the injured limb above heart level if possible and it doesn't cause more pain.","Treat for shock if needed: lay the person down, elevate legs, keep them warm.","Monitor circulation below the injury: check for numbness, tingling, pale or blue skin."]""",
                    warnings = """["Do NOT move someone with a suspected spinal injury unless they are in immediate danger.","Do NOT attempt to straighten or push a bone back into place.","Open fractures (bone visible through skin) require immediate emergency care."]""",
                    severity = "high"
                ),
                FirstAidTopic(
                    category = "Fractures",
                    title = "Sprains and Strains",
                    icon = "ic_fractures",
                    summary = "Treatment for stretched or torn ligaments (sprains) and muscles (strains).",
                    steps = """["Follow the R.I.C.E. method: Rest, Ice, Compression, Elevation.","Rest: Stop using the injured area immediately.","Ice: Apply an ice pack wrapped in cloth for 20 minutes every 2–3 hours.","Compression: Wrap the area with an elastic bandage — snug but not too tight.","Elevation: Raise the injured area above heart level when possible.","Take over-the-counter pain relievers (ibuprofen or acetaminophen) as needed.","Avoid heat, alcohol, and massage for the first 48–72 hours."]""",
                    warnings = """["See a doctor if you cannot bear weight, the joint looks deformed, or there is severe swelling.","If pain and swelling don't improve after 48 hours, seek medical attention."]""",
                    severity = "medium"
                ),
                // POISONING
                FirstAidTopic(
                    category = "Poisoning",
                    title = "Ingested Poison",
                    icon = "ic_poisoning",
                    summary = "When someone swallows a harmful substance like chemicals, medications, or toxic plants.",
                    steps = """["Call Poison Control (1-800-222-1222) or 911 immediately.","Try to identify what was swallowed, how much, and when.","Do NOT induce vomiting unless specifically told to by Poison Control.","If the person is conscious and alert, follow instructions from Poison Control.","If the person is unconscious, place them in the recovery position (on their side).","Save any containers, pills, or plant samples to show medical responders.","Monitor breathing and be prepared to perform CPR if needed."]""",
                    warnings = """["Do NOT give the person anything to eat or drink unless directed by Poison Control.","Do NOT use ipecac syrup — it is no longer recommended.","Do NOT try home remedies like milk or salt water."]""",
                    severity = "critical"
                ),
                FirstAidTopic(
                    category = "Poisoning",
                    title = "Carbon Monoxide Poisoning",
                    icon = "ic_poisoning",
                    summary = "Invisible, odorless gas that can be lethal — requires immediate action.",
                    steps = """["Get everyone out of the building into fresh air immediately.","Call 911 right away.","If someone is unconscious, check for breathing and pulse.","Begin CPR if the person is not breathing.","Do NOT re-enter the building until emergency services declare it safe.","Seek medical evaluation even if symptoms seem mild — CO poisoning can have delayed effects."]""",
                    warnings = """["Symptoms include headache, dizziness, nausea, confusion, and cherry-red skin color.","CO detectors should be installed on every level of your home.","Never use gas-powered generators, grills, or heaters indoors."]""",
                    severity = "critical"
                ),
                FirstAidTopic(
                    category = "Poisoning",
                    title = "Insect Bites and Stings",
                    icon = "ic_poisoning",
                    summary = "Managing reactions to bee stings, spider bites, and other insect encounters.",
                    steps = """["Remove the stinger if visible by scraping it off with a flat edge (like a credit card).","Wash the affected area with soap and water.","Apply a cold compress or ice pack for 10 minutes to reduce swelling.","Apply hydrocortisone cream or calamine lotion to relieve itching.","Take an antihistamine (like Benadryl) for mild allergic reactions.","Watch closely for signs of severe allergic reaction (anaphylaxis).","If the person carries an EpiPen and shows signs of anaphylaxis, help them use it and call 911."]""",
                    warnings = """["Signs of anaphylaxis: difficulty breathing, swelling of face/throat, rapid pulse, dizziness.","If anaphylaxis is suspected, call 911 immediately — this is life-threatening.","Seek medical care for bites from black widows, brown recluses, or scorpions."]""",
                    severity = "medium"
                ),
                // ADDITIONAL TOPICS
                FirstAidTopic(
                    category = "Burns",
                    title = "Chemical Burns",
                    icon = "ic_burns",
                    summary = "Burns caused by contact with acids, alkalis, or other caustic substances.",
                    steps = """["Remove contaminated clothing and jewelry carefully, wearing gloves if possible.","Flush the affected area with large amounts of cool running water for at least 20 minutes.","Call 911 or Poison Control (1-800-222-1222).","Do NOT apply neutralizing agents — just use water.","Cover the area loosely with a dry, sterile bandage after flushing.","If the chemical is in the eyes, flush continuously with clean water for at least 20 minutes."]""",
                    warnings = """["Some chemicals react with water — check the product label if possible.","Do NOT try to neutralize a chemical burn with another substance.","Eye exposure requires immediate and prolonged flushing."]""",
                    severity = "critical"
                ),
                FirstAidTopic(
                    category = "CPR",
                    title = "Using an AED",
                    icon = "ic_cpr",
                    summary = "How to use an Automated External Defibrillator during cardiac emergencies.",
                    steps = """["Turn on the AED — it will give voice instructions.","Expose the person's bare chest and wipe it dry.","Attach the adhesive pads: one on the upper right chest, one on the lower left side.","Make sure no one is touching the person.","The AED will analyze the heart rhythm automatically.","If a shock is advised, press the shock button when prompted.","Immediately resume CPR (30 compressions, 2 breaths) after the shock.","Continue following AED prompts until emergency services arrive."]""",
                    warnings = """["Do NOT use an AED near water or flammable materials.","Remove any medication patches from the chest before placing pads.","Use pediatric pads for children under 8 if available."]""",
                    severity = "critical"
                )
            )

            for (topic in topics) {
                db.openHelper.writableDatabase.execSQL(
                    """INSERT INTO first_aid_topics (category, title, icon, summary, steps, warnings, severity)
                       VALUES (?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(topic.category, topic.title, topic.icon, topic.summary, topic.steps, topic.warnings, topic.severity)
                )
            }
        }

        private fun populateQuiz(db: FirstAidDatabase) {
            val questions = listOf(
                QuizQuestion(
                    question = "What is the first thing you should do if someone is severely bleeding?",
                    optionA = "Apply a tourniquet",
                    optionB = "Call 911 immediately",
                    optionC = "Wash the wound with water",
                    optionD = "Give them water to drink",
                    correctAnswer = "B",
                    explanation = "Always call 911 first for severe bleeding, then apply direct pressure."
                ),
                QuizQuestion(
                    question = "How long should you cool a minor burn under running water?",
                    optionA = "1 minute",
                    optionB = "5 minutes",
                    optionC = "At least 10 minutes",
                    optionD = "30 seconds",
                    correctAnswer = "C",
                    explanation = "Cool a minor burn under cool running water for at least 10 minutes to reduce damage."
                ),
                QuizQuestion(
                    question = "What is the correct rate for CPR chest compressions?",
                    optionA = "60–80 per minute",
                    optionB = "100–120 per minute",
                    optionC = "140–160 per minute",
                    optionD = "80–100 per minute",
                    correctAnswer = "B",
                    explanation = "The correct rate is 100–120 compressions per minute — think of the beat of 'Stayin' Alive.'"
                ),
                QuizQuestion(
                    question = "When performing the Heimlich maneuver, where should you position your fist?",
                    optionA = "On the chest",
                    optionB = "Below the ribcage, above the navel",
                    optionC = "On the back",
                    optionD = "On the stomach below the navel",
                    correctAnswer = "B",
                    explanation = "Place your fist just above the navel and below the ribcage for abdominal thrusts."
                ),
                QuizQuestion(
                    question = "What should you NOT do when treating a suspected fracture?",
                    optionA = "Apply ice to reduce swelling",
                    optionB = "Keep the area still",
                    optionC = "Try to realign the bone",
                    optionD = "Call for medical help",
                    correctAnswer = "C",
                    explanation = "Never try to realign a bone — this can cause additional injury. Immobilize and seek help."
                ),
                QuizQuestion(
                    question = "What is the Poison Control phone number in the US?",
                    optionA = "1-800-222-1222",
                    optionB = "1-800-911-9111",
                    optionC = "1-888-555-1234",
                    optionD = "1-800-123-4567",
                    correctAnswer = "A",
                    explanation = "The US Poison Control number is 1-800-222-1222 — save it in your phone!"
                ),
                QuizQuestion(
                    question = "During a nosebleed, which direction should the person lean?",
                    optionA = "Backward",
                    optionB = "To the side",
                    optionC = "Slightly forward",
                    optionD = "Lie flat",
                    correctAnswer = "C",
                    explanation = "Lean slightly forward to prevent blood from going down the throat."
                ),
                QuizQuestion(
                    question = "What does R.I.C.E. stand for in treating sprains?",
                    optionA = "Rest, Ice, Compress, Exercise",
                    optionB = "Rest, Ice, Compression, Elevation",
                    optionC = "Relax, Ice, Cover, Elevate",
                    optionD = "Rest, Immobilize, Cool, Examine",
                    correctAnswer = "B",
                    explanation = "R.I.C.E. = Rest, Ice, Compression, Elevation — the standard first aid for sprains."
                ),
                QuizQuestion(
                    question = "Should you induce vomiting if someone swallows poison?",
                    optionA = "Yes, always",
                    optionB = "Only if they are conscious",
                    optionC = "Only if told to by Poison Control or 911",
                    optionD = "Yes, using salt water",
                    correctAnswer = "C",
                    explanation = "Never induce vomiting unless specifically instructed by Poison Control or emergency services."
                ),
                QuizQuestion(
                    question = "What is the main symptom of carbon monoxide poisoning?",
                    optionA = "High fever",
                    optionB = "Headache, dizziness, and nausea",
                    optionC = "Rash and itching",
                    optionD = "Sharp chest pain",
                    correctAnswer = "B",
                    explanation = "CO poisoning symptoms include headache, dizziness, nausea, and confusion."
                ),
                QuizQuestion(
                    question = "When using an AED, what should you do before pressing the shock button?",
                    optionA = "Hold the person's hand",
                    optionB = "Pour water on their chest",
                    optionC = "Make sure no one is touching the person",
                    optionD = "Remove the AED pads",
                    correctAnswer = "C",
                    explanation = "Everyone must stand clear of the person before delivering a shock."
                ),
                QuizQuestion(
                    question = "How should you remove a bee stinger?",
                    optionA = "Pull it out with tweezers",
                    optionB = "Scrape it off with a flat edge like a credit card",
                    optionC = "Squeeze it out",
                    optionD = "Leave it in place",
                    correctAnswer = "B",
                    explanation = "Scrape the stinger off with a flat edge to avoid squeezing more venom into the skin."
                ),
                QuizQuestion(
                    question = "For infant choking, what technique should you use?",
                    optionA = "Heimlich maneuver (abdominal thrusts)",
                    optionB = "5 back blows and 5 chest thrusts",
                    optionC = "Finger sweep of the mouth",
                    optionD = "Give water immediately",
                    correctAnswer = "B",
                    explanation = "For infants, alternate 5 back blows and 5 chest thrusts. Never use abdominal thrusts."
                ),
                QuizQuestion(
                    question = "How deep should adult CPR chest compressions be?",
                    optionA = "About 1 inch",
                    optionB = "At least 2 inches",
                    optionC = "About 3 inches",
                    optionD = "As deep as possible",
                    correctAnswer = "B",
                    explanation = "Compress at least 2 inches deep for effective adult CPR."
                ),
                QuizQuestion(
                    question = "What should you do first if you suspect someone has a spinal injury?",
                    optionA = "Help them sit up",
                    optionB = "Roll them on their side",
                    optionC = "Keep them still and call 911",
                    optionD = "Apply a neck brace",
                    correctAnswer = "C",
                    explanation = "Do NOT move someone with a suspected spinal injury. Keep them still and call 911."
                )
            )

            for (q in questions) {
                db.openHelper.writableDatabase.execSQL(
                    """INSERT INTO quiz_questions (question, optionA, optionB, optionC, optionD, correctAnswer, explanation)
                       VALUES (?, ?, ?, ?, ?, ?, ?)""",
                    arrayOf(q.question, q.optionA, q.optionB, q.optionC, q.optionD, q.correctAnswer, q.explanation)
                )
            }
        }
    }
}
