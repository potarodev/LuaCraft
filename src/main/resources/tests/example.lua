local describe, test, expect = Test.describe, Test.test, Test.expect

describe("example", function()
    describe("module1", function()
        test("feature1", function()
            expect(1).to.be.a('number')
            expect('astring').to.equal('astring')
        end)
        test("feature2", function()
            expect(nil).to.exist()
        end)
    end)
end)