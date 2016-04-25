module.exports = function(grunt) {
 grunt.initConfig({
  pkg: grunt.file.readJSON('package.json'),


  jsbeautifier: {
   files: ["Gruntfile.js",
    "**/*.vm",
    "**/*.xml",
    "pom.xml",
    "**/*.js",
    "Gruntfile.js",
    "**/*.css"
   ],
   options: {
    html: {
     fileTypes: [".vm", ".xml"],
     indentChar: " ",
     indentSize: 1,
     preserveNewlines: true,
     unformatted: ["a", "sub", "sup", "b", "i", "u", "strong"]
    },
    js: {
     indentChar: " ",
     indentSize: 1
    }
   }
  }
 });

 grunt.loadNpmTasks("grunt-jsbeautifier");

 grunt.registerTask('default', ['jsbeautifier']);
};
